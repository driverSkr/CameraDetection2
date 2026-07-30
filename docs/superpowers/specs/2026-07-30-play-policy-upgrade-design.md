# Google Play policy compatibility upgrade

## Goal

Update the application so that the production app bundle:

- targets Android 16 (API level 36);
- uses Google Play Billing Library 8.3.0;
- preserves the existing subscription, lifetime purchase, one-time purchase,
  offer selection, purchase acknowledgement, and callback behavior;
- compiles and packages successfully on the current branch.

Real purchase completion must be checked separately with a Google Play license
tester on an internal or closed test track. A local build cannot validate the
Play Store account, product catalog, regional eligibility, or payment UI.

## Root causes

The project currently sets both `COMPILE_SDK_VERSION` and
`TARGET_SDK_VERSION` to 35 and resolves `billing-ktx` 7.0.0.

Compiling the existing GPay module against Billing 8.3.0 fails because the
module still references APIs removed in Billing 8:

- the no-argument `enablePendingPurchases()` call;
- `SkuDetails` query and purchase APIs;
- purchase history query APIs.

The root build also declares Android Gradle Plugin 8.6.0 and Gradle 8.7.
Android API 36 requires a newer supported Android build toolchain.

## Design

### Android API 36 toolchain

Set the shared compile and target SDK properties to 36. Upgrade Android Gradle
Plugin to 8.11.1 and the Gradle wrapper to 8.13. This keeps the project on AGP
8.x, matches the AGP version already recorded in the version catalog, and uses
a toolchain that officially supports API 36 without introducing AGP 9
behavioral migrations.

Do not change the minimum SDK, application ID, version code, version name,
product flavors, signing configuration, or release optimization settings.

### Billing 8.3.0 migration

Set `BILLING_VERSION` to 8.3.0.

Build `BillingClient` with `PendingPurchasesParams` configured for one-time
products and enable automatic service reconnection. Preserve the existing
purchase listener and connection result behavior.

Use the existing `ProductDetails` implementations for subscriptions, lifetime
products, and one-time products. Remove the obsolete `SkuDetails` fallback
implementations and their controller helpers because Billing 8 no longer
provides those APIs. `BillFactory` will return the `ProductDetails`
implementations directly.

Remove the unused purchase-history methods from the internal `GPayImpl`
interface and implementations. Billing 8 removed client-side purchase-history
queries. Existing entitlement checks continue to use `queryPurchasesAsync`,
which returns currently owned purchases and is the correct source for local
entitlement restoration.

No product IDs, base plan IDs, offer IDs, pricing selection rules,
acknowledgement behavior, or application-facing payment callbacks will change.

## Failure handling

Connection failures continue to return the existing disconnect result.
Unfetched or unavailable products continue to produce empty price data or the
existing payment failure callback. Billing service disconnections can recover
through Billing 8 automatic reconnection.

The migration will not fabricate purchase history from active purchases because
that would change the meaning of the removed API. Historical order reporting,
if needed later, must come from a secure backend using Google Play Developer
APIs.

## Verification

Use the Billing 8.3.0 compilation failure already reproduced on the unchanged
source as the red regression signal. After implementation:

1. Compile the GPay production release variant.
2. Run all available local unit tests and Android lint for the production
   release variant.
3. Build the signed `prodVersionRelease` Android App Bundle.
4. Inspect the resolved release dependency graph and confirm
   `billing-ktx:8.3.0` and `billing:8.3.0`.
5. Inspect the merged production release manifest and confirm target SDK 36.
6. Check the final Git diff for unintended source or generated-file changes.

Successful local verification proves that the API 36 and Billing 8 migration is
build-compatible and that the existing payment code compiles against the new
library. End-to-end purchase success still requires uploading the AAB to a Play
test track and testing each configured product with a license tester.
