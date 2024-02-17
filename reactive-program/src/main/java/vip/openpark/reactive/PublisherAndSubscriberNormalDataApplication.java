package vip.openpark.reactive;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * 发布者发布正常数据
 *
 * @author anthony
 * @version 2024/2/17 9:54
 */
@Slf4j
public class PublisherAndSubscriberNormalDataApplication {
	public static void main(String[] args) throws InterruptedException {
		// 1. 定义第一个发布者，用于生产数据
		// 发布者可以是任何东西，例如：数据库查询、IO操作、Web服务、消息队列、定时器、线程池、线程、线程组
		SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
		
		// 2. 定义一个订阅者，用于消费数据
		Flow.Subscriber<String> subscriber = new MySubscriber();
		
		// 3. 绑定订阅关系
		// 绑定操作就是发布者记住了所有的订阅者都有谁，有数据后就会通知订阅者
		// 通知方式是调用订阅者的 onSubscribe、onNext、onError、onComplete 回调
		publisher.subscribe(subscriber);
		
		// 4.发布数据
		for (int i = 0; i < 10; i++) {
			// publisher 发布的所有数据在他的 buffer 区
			publisher.submit("hello world " + i);
		}
		
		// 等待完成
		Thread.sleep(10000);
	}
	
	/**
	 * 定义一个订阅者，用于消费数据
	 */
	static class MySubscriber implements Flow.Subscriber<String> {
		private Flow.Subscription subscription;
		
		@Override
		public void onSubscribe(Flow.Subscription subscription) {
			// 在订阅时发生事件，执行这个回调
			// 订阅者会收到一个 Subscription 对象，可以用来取消订阅
			log.info("当前线程{}，订阅开始了", Thread.currentThread());
			this.subscription = subscription;
			
			// 从上游请求一个数据
			subscription.request(1);
		}
		
		@Override
		public void onNext(String item) {
			// 在收到数据时，执行这个回调
			log.info("当前线程{}，收到数据：{}", Thread.currentThread(), item);
			
			// 处理数据
			log.info("正在处理数据");
			
			// 接收并处理完成一个数据后，请求下一个数据
			this.subscription.request(1);
		}
		
		@Override
		public void onError(Throwable throwable) {
			// 在发生错误时，执行这个回调
			log.error("当前线程{}，发生错误了", Thread.currentThread(), throwable);
		}
		
		@Override
		public void onComplete() {
			// 在完成时，执行这个回调
			log.info("当前线程{}，完成啦", Thread.currentThread());
		}
	}
}