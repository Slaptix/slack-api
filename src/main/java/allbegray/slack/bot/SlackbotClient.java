package allbegray.slack.bot;

import allbegray.slack.RestUtils;
import allbegray.slack.exception.SlackArgumentException;
import allbegray.slack.rtm.ProxyServerInfo;
import allbegray.slack.webapi.SlackWebApiConstants;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SlackbotClient {

	private String slackbotUrl;
	private CloseableHttpClient httpClient;

	public SlackbotClient(String slackbotUrl) {
		this(slackbotUrl, SlackWebApiConstants.DEFAULT_TIMEOUT);
	}

	public SlackbotClient(String slackbotUrl, ProxyServerInfo proxyServerInfo) {
		this(slackbotUrl, SlackWebApiConstants.DEFAULT_TIMEOUT, proxyServerInfo);
	}

	public SlackbotClient(String slackbotUrl, int timeout) {
		this(slackbotUrl, timeout, null);
	}

	public SlackbotClient(String slackbotUrl, int timeout, ProxyServerInfo proxyServerInfo) {
		if (slackbotUrl == null) {
			throw new SlackArgumentException("Missing Slackbot URL Configuration @ SlackApi");

		} else if (!slackbotUrl.contains(".slack.com/services/hooks/slackbot?token=")) {
			throw new SlackArgumentException("Invalid Service URL. Slackbot URL Format: https://{yourteam}.slack.com/services/hooks/slackbot?token={token}");
		}

		this.slackbotUrl = slackbotUrl;
		httpClient = proxyServerInfo != null ? RestUtils.createHttpClient(timeout, proxyServerInfo) : RestUtils.createHttpClient(timeout);
	}

	public void shutdown() {
		if (httpClient != null) try { httpClient.close(); } catch (Exception e) {}
	}

	public String post(String channel, String message) {
		String url = null;
		try {
			url = slackbotUrl + "&channel=" + URLEncoder.encode(channel, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return RestUtils.execute(httpClient, url, new StringEntity(message, "UTF-8"));
	}

}
