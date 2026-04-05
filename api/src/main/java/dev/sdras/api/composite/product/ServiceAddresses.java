package dev.sdras.api.composite.product;

public class ServiceAddresses {
  private final String composite;
  private final String product;
  private final String revview;
  private final String recommendation;

  public ServiceAddresses() {
    composite = null;
    product = null;
    revview = null;
    recommendation = null;
  }

  public ServiceAddresses(
    String compositeAddress,
    String productAddress,
    String reviewAddress,
    String recommendationAddress) {

    this.composite = compositeAddress;
    this.product = productAddress;
    this.revview = reviewAddress;
    this.recommendation = recommendationAddress;
  }

  public String getComposite() {
    return composite;
  }

  public String getProduct() {
    return product;
  }

  public String getRevview() {
    return revview;
  }

  public String getRecommendation() {
    return recommendation;
  }
}
