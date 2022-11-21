package com.ishan.retailservice.invoicefanout.domain;

import java.util.List;

public interface BestSellerService {

  void registerProductPurchase(String product, int quantity);

  List<String> getTopNBestSellingProducts(int n);

}
