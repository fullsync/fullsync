/*
 * Created on Jun 2, 2005
 */
package net.sourceforge.fullsync;

/**
 * @author Michele Aiello
 */
public class SystemDate {

	private static SystemDate _instance;
	
	private long baseTime = -1;
	private long timeOfBaseTime = -1;
	private int speed = 1;
	
	private SystemDate() {
	}
	
	public static SystemDate getInstance() {
		if (_instance == null) {
			_instance = new SystemDate();
		}
		return _instance;
	}
	
	public void setCurrent(long millis) {
		this.baseTime = millis;
		this.timeOfBaseTime = System.currentTimeMillis();
	}
	
	public void setUseSystemTime() {
		this.baseTime = -1;
		this.timeOfBaseTime = -1;
		this.speed = 1;
	}
	
	public void setTimeSpeed(int speed) {
		this.speed = speed;
	}
	
	public long currentTimeMillis() {
		if (baseTime >= 0) {
			return baseTime + ((System.currentTimeMillis() - timeOfBaseTime))*speed;
		}
		else {
			return System.currentTimeMillis();
		}
	}
	
}
