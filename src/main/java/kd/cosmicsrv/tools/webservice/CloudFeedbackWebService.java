/**
 * CloudFeedbackWebService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package kd.cosmicsrv.tools.webservice;

public interface CloudFeedbackWebService extends java.rmi.Remote {
    public String saveSatisfy(String arg0) throws java.rmi.RemoteException;
    public String getFeedbackStatus(String arg0, String arg1, String arg2, String arg3, String arg4) throws java.rmi.RemoteException;
    public String getSystemType(String arg0, String arg1, String arg2, String arg3) throws java.rmi.RemoteException;
    public String upload(String arg0) throws java.rmi.RemoteException;
    public String getProductVersions(String arg0, String arg1) throws java.rmi.RemoteException;
    public String getVersionModules(String arg0, String arg1) throws java.rmi.RemoteException;
    public String getKdriveByCustomer(String arg0, String arg1, String arg2, String arg3, String arg4) throws java.rmi.RemoteException;
    public String listFeedback(String arg0) throws java.rmi.RemoteException;
    public String listDealMethod(String arg0, String arg1, String arg2, String arg3, String arg4) throws java.rmi.RemoteException;
    public String getProducts(String arg0, String arg1) throws java.rmi.RemoteException;
    public String backUpStep(String arg0) throws java.rmi.RemoteException;
    public String getFeedbackOpercache(String arg0, String arg1, String arg2, String arg3, String arg4) throws java.rmi.RemoteException;
    public String getFeedbackInfo(String arg0, String arg1, String arg2, String arg3, String arg4) throws java.rmi.RemoteException;
    public String checkServiceFeeByCust(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6) throws java.rmi.RemoteException;
    public String listFeedbackByCust(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7) throws java.rmi.RemoteException;
    public String listBillType(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5) throws java.rmi.RemoteException;
    public String getProductModules(String arg0, String arg1, String arg2, String arg3, String arg4) throws java.rmi.RemoteException;
    public String saveFeedback(String arg0) throws java.rmi.RemoteException;
    public String getProductDataBase(String arg0, String arg1, String arg2, String arg3, String arg4) throws java.rmi.RemoteException;
}
