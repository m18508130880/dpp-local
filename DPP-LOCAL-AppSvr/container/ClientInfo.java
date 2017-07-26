package container;

public class ClientInfo
{
	private String Type = "";//ÀàÐÍ
	private String Id = "";//µÇÂ½±àºÅ
	private String CName = "";
	
	public ClientInfo(String type, String id) {
		Type = type;
		Id = id;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getCName() {
		return CName;
	}

	public void setCName(String cName) {
		CName = cName;
	}	
	
}