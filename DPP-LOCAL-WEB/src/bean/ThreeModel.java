package bean;

public class ThreeModel {

	private String	id;
	private String	type;
	private String	color;

	private String	positionX;
	private String	positionY;
	private String	positionZ;
	
	private String	rotationX;
	private String	rotationY;
	private String	rotationZ;
	
	// Բ����ģ��
	private String	radiusTop; //�� Բ���嶥�˰뾶. Ĭ��ֵΪ20.
	private String	radiusBottom; //�� Բ����׶˰뾶. Ĭ��ֵΪ20.
	private String	height; //�� Բ����߶�. Ĭ��ֵΪ100.
	private String	radiusSegments; //�� Χ��Բ�����ܳ��ķָ�������. Ĭ��ֵΪ8.
	private String	heightSegments; //�� ��Բ����߶ȵķָ�������. Ĭ��ֵΪ1.
	private String	openEnded; //�� ָʾԲ���������Ǵ򿪻��Ǹ��ǵĲ���ֵ. Ĭ��ֵΪfalse, ��˼�Ǹ���.
	private String	thetaStart; //�� ��һ���ָ���Ŀ�ʼ�Ƕ�, Ĭ��ֵ = 0 (3���ӷ���).
	private String	thetaLength; //�� Բ�����ε�Բ�Ľ�ͨ����Ϊ�ȡ�Ĭ��Ϊ2 * Pi�����γ���һ��������Բ����.
	
	// ������ģ��
	private String	widt; //�� X���ϵ���Ŀ��.
	//private String	height; //�� Y���ϵ���ĸ߶�.
	private String	depth; //�� Z���ϵ�������.
	private String	widthSegments; //�� ��ѡ����. �ؿ����ķָ�������. Ĭ��ֵΪ1.
	//private String	heightSegments; //�� ��ѡ����. �ظ߶���ķָ�������. Ĭ��ֵΪ1.
	private String	depthSegments; //�� ��ѡ����. �������ķָ�������. Ĭ��ֵΪ1.
	
	// ����ģ��
	private String	positionX_W;
	private String	positionY_W;
	private String	positionZ_W;
	
	private String	rotationX_W;
	private String	rotationY_W;
	private String	rotationZ_W;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getPositionX() {
		return positionX;
	}
	public void setPositionX(String positionX) {
		this.positionX = positionX;
	}
	public String getPositionY() {
		return positionY;
	}
	public void setPositionY(String positionY) {
		this.positionY = positionY;
	}
	public String getPositionZ() {
		return positionZ;
	}
	public void setPositionZ(String positionZ) {
		this.positionZ = positionZ;
	}
	public String getRotationX() {
		return rotationX;
	}
	public void setRotationX(String rotationX) {
		this.rotationX = rotationX;
	}
	public String getRotationY() {
		return rotationY;
	}
	public void setRotationY(String rotationY) {
		this.rotationY = rotationY;
	}
	public String getRotationZ() {
		return rotationZ;
	}
	public void setRotationZ(String rotationZ) {
		this.rotationZ = rotationZ;
	}
	public String getRadiusTop() {
		return radiusTop;
	}
	public void setRadiusTop(String radiusTop) {
		this.radiusTop = radiusTop;
	}
	public String getRadiusBottom() {
		return radiusBottom;
	}
	public void setRadiusBottom(String radiusBottom) {
		this.radiusBottom = radiusBottom;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getRadiusSegments() {
		return radiusSegments;
	}
	public void setRadiusSegments(String radiusSegments) {
		this.radiusSegments = radiusSegments;
	}
	public String getHeightSegments() {
		return heightSegments;
	}
	public void setHeightSegments(String heightSegments) {
		this.heightSegments = heightSegments;
	}
	public String getOpenEnded() {
		return openEnded;
	}
	public void setOpenEnded(String openEnded) {
		this.openEnded = openEnded;
	}
	public String getThetaStart() {
		return thetaStart;
	}
	public void setThetaStart(String thetaStart) {
		this.thetaStart = thetaStart;
	}
	public String getThetaLength() {
		return thetaLength;
	}
	public void setThetaLength(String thetaLength) {
		this.thetaLength = thetaLength;
	}
	public String getWidt() {
		return widt;
	}
	public void setWidt(String widt) {
		this.widt = widt;
	}
	public String getDepth() {
		return depth;
	}
	public void setDepth(String depth) {
		this.depth = depth;
	}
	public String getWidthSegments() {
		return widthSegments;
	}
	public void setWidthSegments(String widthSegments) {
		this.widthSegments = widthSegments;
	}
	public String getDepthSegments() {
		return depthSegments;
	}
	public void setDepthSegments(String depthSegments) {
		this.depthSegments = depthSegments;
	}
	public String getPositionX_W() {
		return positionX_W;
	}
	public void setPositionX_W(String positionX_W) {
		this.positionX_W = positionX_W;
	}
	public String getPositionY_W() {
		return positionY_W;
	}
	public void setPositionY_W(String positionY_W) {
		this.positionY_W = positionY_W;
	}
	public String getPositionZ_W() {
		return positionZ_W;
	}
	public void setPositionZ_W(String positionZ_W) {
		this.positionZ_W = positionZ_W;
	}
	public String getRotationX_W() {
		return rotationX_W;
	}
	public void setRotationX_W(String rotationX_W) {
		this.rotationX_W = rotationX_W;
	}
	public String getRotationY_W() {
		return rotationY_W;
	}
	public void setRotationY_W(String rotationY_W) {
		this.rotationY_W = rotationY_W;
	}
	public String getRotationZ_W() {
		return rotationZ_W;
	}
	public void setRotationZ_W(String rotationZ_W) {
		this.rotationZ_W = rotationZ_W;
	}
	
	
}
