# Google Play Policy Upgrade Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Produce a production app bundle that targets Android 16 (API 36) and uses Google Play Billing Library 8.3.0 without changing the app's payment products or callbacks.

**Architecture:** Keep the existing `ProductDetails` payment implementations and remove the obsolete `SkuDetails` compatibility path that Billing 8 no longer exposes. Upgrade the Android build toolchain within AGP 8.x, and validate the resulting production release artifact, merged manifest, and dependency graph.

**Tech Stack:** Android Gradle Plugin 8.11.1, Gradle 8.13, Kotlin 2.1.20, Android API 36, Google Play Billing KTX 8.3.0.

## Global Constraints

- Set both `COMPILE_SDK_VERSION` and `TARGET_SDK_VERSION` to exactly `36`.
- Set `BILLING_VERSION` to exactly `8.3.0`.
- Keep `MIN_SDK_VERSION=28`.
- Do not change the application ID, version code, version name, product flavors, signing configuration, product IDs, base plan IDs, offer IDs, pricing rules, purchase acknowledgement, or payment callbacks.
- Keep Android Gradle Plugin on major version 8.
- Real transactions remain a Google Play internal/closed-track license-tester check; local verification must not claim to validate Play account or catalog state.

---

## File map

- `gradle.properties`: shared compile SDK, target SDK, and Billing versions.
- `build.gradle`: root Android application/library plugin versions.
- `gradle/wrapper/gradle-wrapper.properties`: Gradle distribution used by the project.
- `Libs/GPay/src/main/java/com/ethan/pay/impl/ClientController.kt`: Billing client lifecycle and ProductDetails/current-purchase queries.
- `Libs/GPay/src/main/java/com/ethan/pay/BillFactory.kt`: selects the single supported ProductDetails implementations.
- `Libs/GPay/src/main/java/com/ethan/pay/impl/GPayImpl.kt`: internal payment implementation contract.
- `Libs/GPay/src/main/java/com/ethan/pay/{subs,lifetime,onetime}/*Impl.kt`: ProductDetails payment implementations.
- `Libs/GPay/src/main/java/com/ethan/pay/model/Order.kt`: maps current purchases to app order objects.
- `Libs/GPay/src/main/java/com/ethan/pay/{subs,lifetime,onetime}/*SkuImpl.kt`: obsolete files to delete.

### Task 1: Android API 36 build toolchain

**Files:**
- Modify: `gradle.properties:8-18`
- Modify: `build.gradle:3-6`
- Modify: `gradle/wrapper/gradle-wrapper.properties:4`

**Interfaces:**
- Consumes: existing Gradle property names `COMPILE_SDK_VERSION` and `TARGET_SDK_VERSION`.
- Produces: every Android module compiles with API 36 under AGP 8.11.1 and Gradle 8.13.

- [ ] **Step 1: Run the policy property check and verify it fails**

```powershell
$properties = Get-Content -Raw gradle.properties
if ($properties -notmatch '(?m)^COMPILE_SDK_VERSION=36$') { throw 'compileSdk is not 36' }
if ($properties -notmatch '(?m)^TARGET_SDK_VERSION=36$') { throw 'targetSdk is not 36' }
```

Expected: FAIL with `compileSdk is not 36`.

- [ ] **Step 2: Set the SDK and build tool versions**

Change `gradle.properties` to:

```properties
COMPILE_SDK_VERSION=36
MIN_SDK_VERSION=28
TARGET_SDK_VERSION=36
```

Change both root plugin declarations in `build.gradle` to:

```groovy
id 'com.android.application' version '8.11.1' apply false
id 'com.android.library' version '8.11.1' apply false
```

Change the wrapper URL to:

```properties
distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-8.13-bin.zip
```

- [ ] **Step 3: Run the property check and Gradle version check**

```powershell
$properties = Get-Content -Raw gradle.properties
if ($properties -notmatch '(?m)^COMPILE_SDK_VERSION=36$') { throw 'compileSdk is not 36' }
if ($properties -notmatch '(?m)^TARGET_SDK_VERSION=36$') { throw 'targetSdk is not 36' }
.\gradlew.bat --version
```

Expected: property checks return normally and Gradle reports `Gradle 8.13`.

- [ ] **Step 4: Compile the production release manifest**

```powershell
.\gradlew.bat :app:processProdVersionReleaseMainManifest --no-daemon
```

Expected: `BUILD SUCCESSFUL` without an unsupported compile SDK warning.

- [ ] **Step 5: Commit the toolchain upgrade**

```powershell
git add -- gradle.properties build.gradle gradle/wrapper/gradle-wrapper.properties
git commit -m "build: target Android 16"
```

### Task 2: Google Play Billing 8.3.0 migration

**Files:**
- Modify: `gradle.properties:18`
- Modify: `Libs/GPay/src/main/java/com/ethan/pay/impl/ClientController.kt`
- Modify: `Libs/GPay/src/main/java/com/ethan/pay/BillFactory.kt`
- Modify: `Libs/GPay/src/main/java/com/ethan/pay/impl/GPayImpl.kt`
- Modify: `Libs/GPay/src/main/java/com/ethan/pay/subs/SubscribeImpl.kt`
- Modify: `Libs/GPay/src/main/java/com/ethan/pay/lifetime/LifeTimeImpl.kt`
- Modify: `Libs/GPay/src/main/java/com/ethan/pay/onetime/OneTimeImpl.kt`
- Modify: `Libs/GPay/src/main/java/com/ethan/pay/model/Order.kt`
- Delete: `Libs/GPay/src/main/java/com/ethan/pay/subs/SubscribeSkuImpl.kt`
- Delete: `Libs/GPay/src/main/java/com/ethan/pay/lifetime/LifeTimeSkuImpl.kt`
- Delete: `Libs/GPay/src/main/java/com/ethan/pay/onetime/OneTimeSkuImpl.kt`

**Interfaces:**
- Consumes: `BillingClient`, existing `ProductDetails` queries, `GPayImpl`, and `OrderInfo`.
- Produces: Billing 8.3.0-compatible `BillFactory.getSubscribe()`, `getLifeTime()`, and `getOneTime()` implementations with unchanged return types and purchase callbacks.

- [ ] **Step 1: Set Billing 8.3.0 and run the known failing compile**

Change `gradle.properties` to:

```properties
BILLING_VERSION=8.3.0
```

Run:

```powershell
.\gradlew.bat :Libs:GPay:compileProdVersionReleaseKotlin --no-daemon
```

Expected: FAIL on removed `SkuDetailsResult`, `queryPurchaseHistory`, `querySkuDetails`, and the no-argument `enablePendingPurchases()` call.

- [ ] **Step 2: Migrate BillingClient construction**

In `ClientController.kt`, add:

```kotlin
import com.android.billingclient.api.PendingPurchasesParams
```

Construct the client as:

```kotlin
client = BillingClient.newBuilder(context)
    .setListener { result, purchases ->
        onPurchaseListener?.onPurchase(result, purchases)
    }
    .enablePendingPurchases(
        PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts()
            .build()
    )
    .enableAutoServiceReconnection()
    .build()
```

Remove imports and methods for `PurchaseHistoryRecord`,
`QueryPurchaseHistoryParams`, `SkuDetailsParams`, `SkuDetailsResult`,
`queryPurchaseHistory`, `querySkuDetails`, and the String overload of
`queryPurchasesAsync`. Keep `queryProductDetails(...)` and
`queryPurchase(productType: String)`.

- [ ] **Step 3: Remove the obsolete SkuDetails selection path**

In `BillFactory.kt`, keep one cached instance for each payment type:

```kotlin
private var subscribeImpl: GPayImpl? = null
private var lifeTimeImpl: GPayImpl? = null
private var oneTimeImpl: GPayImpl? = null

fun getSubscribe(): GPayImpl {
    if (subscribeImpl == null) subscribeImpl = SubscribeImpl()
    return subscribeImpl!!
}

fun getLifeTime(): GPayImpl {
    if (lifeTimeImpl == null) lifeTimeImpl = LifeTimeImpl()
    return lifeTimeImpl!!
}

fun getOneTime(): GPayImpl {
    if (oneTimeImpl == null) oneTimeImpl = OneTimeImpl()
    return oneTimeImpl!!
}
```

Remove `isSupport()` and all Sku implementation imports and fields. Delete the
three `*SkuImpl.kt` files listed above.

- [ ] **Step 4: Remove the purchase-history contract and preserve repeatable one-time purchases**

Remove these declarations from `GPayImpl.kt` and their overrides/imports from
all three ProductDetails implementations:

```kotlin
suspend fun getPurchaseHistory(): MutableList<PurchaseHistoryRecord>
suspend fun getPurchaseHistory2OrderInfo(): List<OrderInfo>
```

In `OneTimeImpl.launchBilling`, replace the history lookup with a current-owned
purchase lookup:

```kotlin
val ownedPurchase = queryPurchase().firstOrNull { it.goodsId == goods.productId }
ownedPurchase?.token?.let { handlePurchase(it) }
```

This consumes an outstanding repeatable one-time purchase before launching a
new flow, preserving the intent of the old history-based code while using the
Billing 8-supported current-purchases API.

Remove `createOrderInfo4His(...)` and its `PurchaseHistoryRecord` import from
`Order.kt`; keep `createOrderInfo(...)`.

- [ ] **Step 5: Verify no removed API remains**

```powershell
$matches = rg -n 'PurchaseHistoryRecord|queryPurchaseHistory|SkuDetails|SkuType|querySku|enablePendingPurchases\(\)' Libs/GPay/src/main/java
if ($LASTEXITCODE -eq 0) { throw "Removed Billing APIs remain:`n$matches" }
```

Expected: `rg` returns no matches and the check completes normally.

- [ ] **Step 6: Compile the Billing module and app**

```powershell
.\gradlew.bat :Libs:GPay:compileProdVersionReleaseKotlin :app:compileProdVersionReleaseKotlin --no-daemon
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 7: Commit the Billing migration**

```powershell
git add -- gradle.properties Libs/GPay
git commit -m "fix: migrate payments to Billing 8.3"
```

### Task 3: Production release verification

**Files:**
- Verify: `app/build/outputs/bundle/prodVersionRelease/app-prodVersion-release.aab`
- Verify: `app/build/intermediates/merged_manifest/prodVersionRelease/processProdVersionReleaseMainManifest/AndroidManifest.xml`

**Interfaces:**
- Consumes: the API 36 build configuration and Billing 8.3.0 payment module.
- Produces: a release AAB plus recorded evidence of its SDK and Billing dependency versions.

- [ ] **Step 1: Run all local unit tests**

```powershell
.\gradlew.bat test --no-daemon
```

Expected: `BUILD SUCCESSFUL` with zero failed tests.

- [ ] **Step 2: Run production release lint**

```powershell
.\gradlew.bat :app:lintProdVersionRelease --no-daemon
```

Expected: `BUILD SUCCESSFUL` with no fatal lint errors.

- [ ] **Step 3: Build the signed production AAB**

```powershell
.\gradlew.bat :app:bundleProdVersionRelease --no-daemon
```

Expected: `BUILD SUCCESSFUL` and
`app/build/outputs/bundle/prodVersionRelease/app-prodVersion-release.aab`
exists and has nonzero length.

- [ ] **Step 4: Verify resolved Billing artifacts**

```powershell
$dependencies = .\gradlew.bat :Libs:GPay:dependencies --configuration prodVersionReleaseRuntimeClasspath --no-daemon
if ($dependencies -notmatch 'billing-ktx:8\.3\.0') { throw 'billing-ktx 8.3.0 not resolved' }
if ($dependencies -notmatch 'billing:8\.3\.0') { throw 'billing 8.3.0 not resolved' }
```

Expected: both assertions return normally.

- [ ] **Step 5: Verify the merged manifest and artifact**

```powershell
$manifest = Get-Content -Raw 'app/build/intermediates/merged_manifest/prodVersionRelease/processProdVersionReleaseMainManifest/AndroidManifest.xml'
if ($manifest -notmatch 'targetSdkVersion="36"') { throw 'merged manifest does not target API 36' }
$aab = Get-Item 'app/build/outputs/bundle/prodVersionRelease/app-prodVersion-release.aab'
if ($aab.Length -le 0) { throw 'AAB is empty' }
```

Expected: both assertions return normally.

- [ ] **Step 6: Review repository scope**

```powershell
git diff --check
git status --short
git log -3 --oneline
```

Expected: no whitespace errors, no generated build outputs in Git status, and
only the design/plan, toolchain, and Billing migration commits are new.

- [ ] **Step 7: Record the required device validation handoff**

Upload the generated AAB to a Google Play internal or closed test track. With a
license tester, verify connection, price loading, subscription offer selection,
purchase launch, cancellation, success callback, acknowledgement/consumption,
and restore-current-entitlement behavior for every configured product.
