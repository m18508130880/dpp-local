package net;

public class MsgHeadBean
{
	int unMsgLen = 0;
	int unMsgCode = 0;
	int unStatus = 0;
	int unMsgSeq = 0;
	int unReserve = 0;
	public int getUnMsgCode() {
		return unMsgCode;
	}
	public void setUnMsgCode(int unMsgCode) {
		this.unMsgCode = unMsgCode;
	}
	public int getUnMsgLen() {
		return unMsgLen;
	}
	public void setUnMsgLen(int unMsgLen) {
		this.unMsgLen = unMsgLen;
	}
	public int getUnMsgSeq() {
		return unMsgSeq;
	}
	public void setUnMsgSeq(int unMsgSeq) {
		this.unMsgSeq = unMsgSeq;
	}
	public int getUnReserve() {
		return unReserve;
	}
	public void setUnReserve(int unReserve) {
		this.unReserve = unReserve;
	}
	public int getUnStatus() {
		return unStatus;
	}
	public void setUnStatus(int unStatus) {
		this.unStatus = unStatus;
	}
}