package kd.cosmicsrv.formplugin;

import java.util.EventObject;

import kd.bos.form.control.Button;
import kd.bos.form.control.Control;
import kd.bos.form.control.events.UploadEvent;
import kd.bos.form.control.events.UploadListener;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.url.UrlService;

public class CosmicsrvAppParamPlugin extends AbstractFormPlugin implements UploadListener {
	@Override
	public void registerListener(EventObject e) {

		super.registerListener(e);
		Button deployguide = this.getView().getControl("deployguide");
		deployguide.addUploadListener(this);
		Button opsguide = this.getView().getControl("opsguide");
		opsguide.addUploadListener(this);
	}

	@Override
	public void afterUpload(UploadEvent evt) {
		Control source = (Control) evt.getSource();
		String key = source.getKey();
		if ("deployguide".equals(key)) {
			Object[] urls = evt.getUrls();
			if ((urls != null) && (urls.length > 0)) {
				String filePath = (String) urls[0];
				String fullUrl = UrlService.getAttachmentFullUrl(filePath);
				getModel().setValue("deployguide_url", fullUrl);
			}
		}
		if ("opsguide".equals(key)) {
			Object[] urls = evt.getUrls();
			if ((urls != null) && (urls.length > 0)) {
				String filePath = (String) urls[0];
				String fullUrl = UrlService.getAttachmentFullUrl(filePath);
				getModel().setValue("opsguide_url", fullUrl);
			}
		}
	}

}
