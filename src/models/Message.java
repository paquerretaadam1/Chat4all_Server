package models;

import net.sf.json.JSONObject;

public class Message {
	public final static String LOGIN = "LOGIN";
	public final static String LOGOUT = "LOGOUT";
	public final static String SendMSG = "SENDMSG";
	public final static String SINGIN = "SINGIN";
	String type;
	String body;
	String from;
	String to;
	
	public Message() {
		// TODO Auto-generated constructor stub
	}
	
	
	public Message(String type, String body, String from, String to) {
		super();
		this.type = type;
		this.body = body;
		this.from = from;
		this.to = to;
	}
 
 
	public static Message getInstance(String jsonStr)
	{
		Message instance = null;
		try { 
			JSONObject object = JSONObject.fromString(jsonStr); 
			instance = (Message) JSONObject.toBean(object, Message.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instance;
	}
	
	public String toJSON()
	{
		JSONObject object = JSONObject.fromBean(this);
		return object.toString();
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public static String getLogin() {
		return LOGIN;
	}
	public static String getLogout() {
		return LOGOUT;
	}
	
}
