package kd.cosmicsrv.tools.webservice;

public class CloudFeedbackWebServiceProxy implements CloudFeedbackWebService {
  private String _endpoint = null;
  private CloudFeedbackWebService cloudFeedbackWebService = null;
  
  public CloudFeedbackWebServiceProxy() {
    _initCloudFeedbackWebServiceProxy();
  }
  
  public CloudFeedbackWebServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initCloudFeedbackWebServiceProxy();
  }
  
  private void _initCloudFeedbackWebServiceProxy() {
    try {
      cloudFeedbackWebService = (new CloudFeedbackWebServiceImplServiceLocator()).getCloudFeedbackWebServiceImplPort();
      if (cloudFeedbackWebService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)cloudFeedbackWebService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)cloudFeedbackWebService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (cloudFeedbackWebService != null)
      ((javax.xml.rpc.Stub)cloudFeedbackWebService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public CloudFeedbackWebService getCloudFeedbackWebService() {
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService;
  }
  
  public String saveSatisfy(String arg0) throws java.rmi.RemoteException{
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService.saveSatisfy(arg0);
  }
  
  public String getFeedbackStatus(String arg0, String arg1, String arg2, String arg3, String arg4) throws java.rmi.RemoteException{
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService.getFeedbackStatus(arg0, arg1, arg2, arg3, arg4);
  }
  
  public String getSystemType(String arg0, String arg1, String arg2, String arg3) throws java.rmi.RemoteException{
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService.getSystemType(arg0, arg1, arg2, arg3);
  }
  
  public String upload(String arg0) throws java.rmi.RemoteException{
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService.upload(arg0);
  }
  
  public String getProductVersions(String arg0, String arg1) throws java.rmi.RemoteException{
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService.getProductVersions(arg0, arg1);
  }
  
  public String getVersionModules(String arg0, String arg1) throws java.rmi.RemoteException{
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService.getVersionModules(arg0, arg1);
  }
  
  public String getKdriveByCustomer(String arg0, String arg1, String arg2, String arg3, String arg4) throws java.rmi.RemoteException{
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService.getKdriveByCustomer(arg0, arg1, arg2, arg3, arg4);
  }
  
  public String listFeedback(String arg0) throws java.rmi.RemoteException{
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService.listFeedback(arg0);
  }
  
  public String listDealMethod(String arg0, String arg1, String arg2, String arg3, String arg4) throws java.rmi.RemoteException{
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService.listDealMethod(arg0, arg1, arg2, arg3, arg4);
  }
  
  public String getProducts(String arg0, String arg1) throws java.rmi.RemoteException{
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService.getProducts(arg0, arg1);
  }
  
  public String backUpStep(String arg0) throws java.rmi.RemoteException{
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService.backUpStep(arg0);
  }
  
  public String getFeedbackOpercache(String arg0, String arg1, String arg2, String arg3, String arg4) throws java.rmi.RemoteException{
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService.getFeedbackOpercache(arg0, arg1, arg2, arg3, arg4);
  }
  
  public String getFeedbackInfo(String arg0, String arg1, String arg2, String arg3, String arg4) throws java.rmi.RemoteException{
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService.getFeedbackInfo(arg0, arg1, arg2, arg3, arg4);
  }
  
  public String checkServiceFeeByCust(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6) throws java.rmi.RemoteException{
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService.checkServiceFeeByCust(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
  }
  
  public String listFeedbackByCust(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7) throws java.rmi.RemoteException{
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService.listFeedbackByCust(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
  }
  
  public String listBillType(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5) throws java.rmi.RemoteException{
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService.listBillType(arg0, arg1, arg2, arg3, arg4, arg5);
  }
  
  public String getProductModules(String arg0, String arg1, String arg2, String arg3, String arg4) throws java.rmi.RemoteException{
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService.getProductModules(arg0, arg1, arg2, arg3, arg4);
  }
  
  public String saveFeedback(String arg0) throws java.rmi.RemoteException{
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService.saveFeedback(arg0);
  }
  
  public String getProductDataBase(String arg0, String arg1, String arg2, String arg3, String arg4) throws java.rmi.RemoteException{
    if (cloudFeedbackWebService == null)
      _initCloudFeedbackWebServiceProxy();
    return cloudFeedbackWebService.getProductDataBase(arg0, arg1, arg2, arg3, arg4);
  }
  
  
}