package falseapple.GeneralLib.core.interfaces;

/**
 * @author yueh
 * 開關切換器
 */
public interface Switchable {

	/**
	 * 啟動
	 * @param options
	 * @return true if start success
	 */
	boolean start(Object... options);
	
	/**
	 * 停止
	 * @return true if stop success
	 */
	boolean stop();
	
	/**
	 * 是否運做中
	 * @return 
	 */
	boolean isRunning();
}
