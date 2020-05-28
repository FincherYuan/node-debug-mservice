/**
 * CloudFeedbackWebServiceImplServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package kd.cosmicsrv.tools.webservice;

import kd.cosmicsrv.tools.CosmicsrvConfigUtil;

public class CloudFeedbackWebServiceImplServiceLocator extends org.apache.axis.client.Service implements CloudFeedbackWebServiceImplService {

    public CloudFeedbackWebServiceImplServiceLocator() {
    }


    public CloudFeedbackWebServiceImplServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CloudFeedbackWebServiceImplServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CloudFeedbackWebServiceImplPort
    
    private static String CloudFeedbackWebServiceImplPort_address;

 	static {
 		//CloudFeedbackWebServiceImplPort_address = "http://ksmtest.kingdee.com:8000/services/CloudFeedbackWebService";
 		CloudFeedbackWebServiceImplPort_address = CosmicsrvConfigUtil.safeGetValue("ksm_headurl");
 	}


    public String getCloudFeedbackWebServiceImplPortAddress() {
        return CloudFeedbackWebServiceImplPort_address;
    }

    // The WSDD service name defaults to the port name.
    private String CloudFeedbackWebServiceImplPortWSDDServiceName = "CloudFeedbackWebServiceImplPort";

    public String getCloudFeedbackWebServiceImplPortWSDDServiceName() {
        return CloudFeedbackWebServiceImplPortWSDDServiceName;
    }

    public void setCloudFeedbackWebServiceImplPortWSDDServiceName(String name) {
        CloudFeedbackWebServiceImplPortWSDDServiceName = name;
    }

    public CloudFeedbackWebService getCloudFeedbackWebServiceImplPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CloudFeedbackWebServiceImplPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCloudFeedbackWebServiceImplPort(endpoint);
    }

    public CloudFeedbackWebService getCloudFeedbackWebServiceImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            CloudFeedbackWebServiceImplServiceSoapBindingStub _stub = new CloudFeedbackWebServiceImplServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getCloudFeedbackWebServiceImplPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCloudFeedbackWebServiceImplPortEndpointAddress(String address) {
        CloudFeedbackWebServiceImplPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (CloudFeedbackWebService.class.isAssignableFrom(serviceEndpointInterface)) {
                CloudFeedbackWebServiceImplServiceSoapBindingStub _stub = new CloudFeedbackWebServiceImplServiceSoapBindingStub(new java.net.URL(CloudFeedbackWebServiceImplPort_address), this);
                _stub.setPortName(getCloudFeedbackWebServiceImplPortWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("CloudFeedbackWebServiceImplPort".equals(inputPortName)) {
            return getCloudFeedbackWebServiceImplPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://service.integration.icrm.kingdee.com/", "CloudFeedbackWebServiceImplService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://service.integration.icrm.kingdee.com/", "CloudFeedbackWebServiceImplPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("CloudFeedbackWebServiceImplPort".equals(portName)) {
            setCloudFeedbackWebServiceImplPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
