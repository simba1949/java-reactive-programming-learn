package vip.openpark.reactive;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * 添加处理器实现发布订阅及中间操作
 *
 * @author anthony
 * @version 2024/2/17 10:08
 */
@Slf4j
public class AddProcessorApplication {
	public static void main(String[] args) throws InterruptedException {
		// 1. 定义一个发布者，用于生产数据
		SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
		
		// 2. 定义一个处理器，用于中间操作
		MyProcessor myProcessor = new MyProcessor();
		
		// 2. 定义一个订阅者，用于消费数据
		Flow.Subscriber<String> subscriber = new MySubscriber();
		
		// 3. 绑定订阅关系
		// 生产者<-中间处理器<...-<-中间处理器<-消费者
		publisher.subscribe(myProcessor);
		myProcessor.subscribe(subscriber);
		
		// 4.发布数据
		for (int i = 0; i < 5; i++) {
			// publisher 发布的所有数据在他的 buffer 区
			publisher.submit("hello world " + i);
		}
		
		// 等待完成
		Thread.sleep(10000);
	}
	
	/**
	 * 基于 SubmissionPublisher 自定义一个处理器
	 */
	static class MyProcessor extends SubmissionPublisher<String> implements Flow.Processor<String, String> {
		private Flow.Subscription subscription;
		
		@Override
		public void onSubscribe(Flow.Subscription subscription) {
			log.info("onSubscribe");
			this.subscription = subscription;
			// 请求数据
			subscription.request(1);
		}
		
		@Override
		public void onNext(String item) {
			log.info("onNext:{}", item);
			
			// 中间处理器这里做一个添加前缀的操作
			super.submit("中间处理器的前缀：" + item);
			
			// 请求数据
			subscription.request(1);
		}
		
		@Override
		public void onError(Throwable throwable) {
			log.error("onError", throwable);
		}
		
		@Override
		public void onComplete() {
			log.info("onComplete");
		}
	}
	
	/**
	 * 自定义一个订阅者
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