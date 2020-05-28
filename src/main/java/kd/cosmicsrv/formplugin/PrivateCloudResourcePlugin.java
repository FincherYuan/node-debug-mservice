package kd.cosmicsrv.formplugin;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.EventObject;

import com.alibaba.dubbo.common.URL;

import kd.bos.consts.OrgViewTypeConst;
import kd.bos.entity.param.AppParam;
import kd.bos.form.control.Control;
import kd.bos.form.control.Label;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.servicehelper.parameter.SystemParamServiceHelper;

public class PrivateCloudResourcePlugin extends AbstractFormPlugin {
	@Override
	public void registerListener(EventObject e) {

		super.registerListener(e);
		Label deploytools_url = this.getView().getControl("deploytools_url");
		deploytools_url.addClickListener(this);
		
		Label deployguide_url = this.getView().getControl("deployguide_url");
		deployguide_url.addClickListener(this);
		
		Label opsguide_url = this.getView().getControl("opsguide_url");
		opsguide_url.addClickListener(this);
		
	}
	
	
	
	@Override
	public void click(EventObject evt) {
		// TODO Auto-generated method stub
		Control control = (Control)evt.getSource();
        String key = control.getKey();
		AppParam appParam=new AppParam();
		appParam.setAppId("/V161QN=0YH5");
		appParam.setActBookId(0L);
		appParam.setOrgId(100000L);
		appParam.setViewType(OrgViewTypeConst.Admin);
		String url=(String) SystemParamServiceHelper.loadAppParameterFromCache(appParam, key);
		try {
			this.getView().download(URLEncoder.encode(url,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
