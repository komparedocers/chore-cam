package com.choreocam.app.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.choreocam.app.ChoreoCamApplication;
import com.choreocam.app.R;
import com.choreocam.app.database.AppDatabase;
import com.choreocam.app.models.User;
import com.google.android.material.button.MaterialButton;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.ProductDetails;
import java.util.ArrayList;
import java.util.List;

public class ProUpgradeActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    private MaterialButton monthlyBtn;
    private MaterialButton yearlyBtn;
    private MaterialButton restoreBtn;

    private BillingClient billingClient;
    private AppDatabase database;
    private List<ProductDetails> productDetailsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_upgrade);

        database = ChoreoCamApplication.getDatabase();

        initializeViews();
        setupToolbar();
        setupBilling();
        setupClickListeners();
    }

    private void initializeViews() {
        monthlyBtn = findViewById(R.id.monthlyBtn);
        yearlyBtn = findViewById(R.id.yearlyBtn);
        restoreBtn = findViewById(R.id.restoreBtn);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.pro_title);
        }
    }

    private void setupBilling() {
        if (!ChoreoCamApplication.getConfigManager().isIAPEnabled()) {
            Toast.makeText(this, "In-app purchases are not available", Toast.LENGTH_SHORT).show();
            return;
        }

        billingClient = BillingClient.newBuilder(this)
            .setListener(this)
            .enablePendingPurchases()
            .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    queryProducts();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request
            }
        });
    }

    private void queryProducts() {
        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();

        productList.add(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(ChoreoCamApplication.getConfigManager().getProMonthlySku())
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        );

        productList.add(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(ChoreoCamApplication.getConfigManager().getProYearlySku())
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, prodDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                productDetailsList = prodDetailsList;
            }
        });
    }

    private void setupClickListeners() {
        monthlyBtn.setOnClickListener(v -> {
            purchaseProduct(ChoreoCamApplication.getConfigManager().getProMonthlySku());
        });

        yearlyBtn.setOnClickListener(v -> {
            purchaseProduct(ChoreoCamApplication.getConfigManager().getProYearlySku());
        });

        restoreBtn.setOnClickListener(v -> {
            restorePurchases();
        });
    }

    private void purchaseProduct(String productId) {
        if (productDetailsList == null || productDetailsList.isEmpty()) {
            Toast.makeText(this, "Products not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }

        ProductDetails productDetails = null;
        for (ProductDetails details : productDetailsList) {
            if (details.getProductId().equals(productId)) {
                productDetails = details;
                break;
            }
        }

        if (productDetails == null) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            return;
        }

        List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = new ArrayList<>();
        productDetailsParamsList.add(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        );

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build();

        billingClient.launchBillingFlow(this, billingFlowParams);
    }

    private void restorePurchases() {
        billingClient.queryPurchasesAsync(
            BillingClient.ProductType.SUBS,
            (billingResult, purchases) -> {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    if (purchases != null && !purchases.isEmpty()) {
                        handlePurchases(purchases);
                        Toast.makeText(this, "Purchases restored!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No purchases to restore", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases);
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(this, "Purchase canceled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Purchase failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void handlePurchases(List<Purchase> purchases) {
        for (Purchase purchase : purchases) {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                // Upgrade user to Pro
                new Thread(() -> {
                    User currentUser = database.userDao().getCurrentUser();
                    if (currentUser != null) {
                        currentUser.setPro(true);
                        database.userDao().update(currentUser);
                    }
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Welcome to Pro!", Toast.LENGTH_LONG).show();
                        finish();
                    });
                }).start();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        if (billingClient != null) {
            billingClient.endConnection();
        }
        super.onDestroy();
    }
}
