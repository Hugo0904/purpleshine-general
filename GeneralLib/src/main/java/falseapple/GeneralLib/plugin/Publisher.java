package falseapple.GeneralLib.plugin;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author yueh
 *
 * 發佈者
 *
 * @param <SData>
 */
public class Publisher<SData> {
	
	private final CopyOnWriteArrayList<Processor<? super SData, ?>> processors = new CopyOnWriteArrayList<>();
	
	/**
	 * 註冊處理器
	 * @param processor
	 */
	public void register(Processor<? super SData, ?> processor) {
		this.processors.add(Objects.requireNonNull(processor));
		processor.onRegister(this);
	}
	
	/**
	 * 取消註冊處理器
	 * @param processor
	 */
	public void unRegister(Processor<? super SData, ?> processor) {
		this.processors.remove(processor);
	}
	
	/**
	 * 取消所有註冊處理器
	 * @param processor
	 */
	public void unRegisters() {
		this.processors.clear();
	}
	
	/**
	 * 發布訊息
	 * @param data
	 */
	public void publish(SData data) {
		Objects.requireNonNull(data);
		processors.forEach(processor -> processor.onProcessor(data));
	}
	
	
	/**
	 * 
	 * @author yueh
	 *
	 * 訂閱資料處理轉換器
	 *
	 * @param <TData>
	 */
	static public abstract class Processor<SData, TData> {
		
		private final CopyOnWriteArrayList<Publisher<? extends SData>> publishers = new CopyOnWriteArrayList<>();
		private final ConcurrentLinkedQueue<SData> temp = new ConcurrentLinkedQueue<>();
		private final AtomicInteger pause = new AtomicInteger(0);
		private final AtomicInteger count = new AtomicInteger(0);
		private final ExecutorService executor;
		
		private Subscriber<? super TData> subscriber;
		
		/**
		 * Constructor
		 * @param subscriber 訂閱者
		 */
		public Processor(final Subscriber<? super TData> subscriber) {
			this(subscriber, 1);
		}
		
		/**
		 * Constructor
		 * @param subscriber 訂閱者
		 * @param poolSize 線程池核心數量
		 */
		public Processor(final Subscriber<? super TData> subscriber, final int poolSize) {
			this(subscriber, (poolSize > 1 ? Executors.newFixedThreadPool(poolSize) : Executors.newSingleThreadExecutor()));
		}
		
		/**
		 * Constructor
		 * @param subscriber 訂閱者
		 * @param executor 線程池
		 */
		public Processor(final Subscriber<? super TData> subscriber, final ExecutorService executor) {
			this.subscriber = Objects.requireNonNull(subscriber);
			this.executor = Objects.requireNonNull(executor);
		}
		
		/**
		 * 當處理器成功向發佈者註冊所引發的事件
		 * @param publisher
		 */
		protected void onRegister(Publisher<? extends SData> publisher) {
			this.publishers.add(publisher);
			this.subscriber.onSubscribe(publisher, this);
		}
		
		/**
		 * 當發佈消息時開始處理的事件
		 * @param data
		 */
		protected void onProcessor(SData data) {
			switch (this.pause.get()) {
			case 0:	// 正常
				count.incrementAndGet();
				CompletableFuture.supplyAsync(() -> processData(data), executor)
					.whenCompleteAsync((result, err) -> {
						if (Objects.isNull(err))
							subscriber.onNext(this, result);
						else
							subscriber.onError(this, err);
						
						count.decrementAndGet();
					});
				break;
				
			case 1: // 保存
				temp.offer(data);
				break;
				
			case 2: // 跳過
				return;
			}
		}
		
		/**
		 * 暫停處理訂閱訊息
		 */
		public void pause(boolean skipAllData) {
			pause.set(skipAllData ? 2 : 1);
		}
		
		/**
		 * 繼續處理訂閱訊息
		 */
		public void resume() {
			pause.set(0);
			SData data;
			while (Objects.nonNull(data = temp.poll())) {
				onProcessor(data);
			}
		}
		
		public final void clearTemp() {
			temp.clear();
		}
		
		/**
		 * 取消這個處理器, 並退出訂閱
		 * @param showDown 是否關閉程池 0 = 不需要, 1 = 正常關閉, 2 = 強制關閉
		 */
		public final void cancel(int showDown) {
			if (showDown == 1)
				executor.shutdown();
			else if (showDown == 2)
				executor.shutdownNow();
			
			clearTemp();
			publishers.forEach(p -> p.unRegister(this));
			subscriber.onComplete();
		}
		
		/**
		 * 取得當前處理器的處理中的數量
		 * @return
		 */
		public final int getActiveCount() {
			return count.get();
		}
		
		/**
		 * 訂閱資料轉換
		 * @param data
		 * @return
		 */
		protected abstract TData processData(SData data);
	}
	
	
	/**
	 * 
	 * @author yueh
	 *
	 * 訂閱者
	 *
	 * @param <TData>
	 */
	static public interface Subscriber<TData> {
		 
		/**
		 * 當向處理器訂閱成功時
		 * @param publisher
		 */
		public void onSubscribe(Publisher<?> publisher, Processor<?, ? extends TData> processor);
		
		/**
		 * 當有事件發佈時
		 * @param item
		 */
		public void onNext(Processor<?, ? extends TData> processor, TData data);
		
		/**
		 * 當退出訂閱時
		 */
		public void onComplete();
		
		/**
		 * 當有錯誤承接時
		 * @param t
		 */
		public void onError(Processor<?, ? extends TData> processor, Throwable t);
	}
}

