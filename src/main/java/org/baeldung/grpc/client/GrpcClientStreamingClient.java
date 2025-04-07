package org.baeldung.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.baeldung.grpc.HelloRequest;
import org.baeldung.grpc.HelloResponse;
import org.baeldung.grpc.HelloServiceGrpc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GrpcClientStreamingClient {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 8085)
                .usePlaintext()
                .build();

        HelloServiceGrpc.HelloServiceStub stub = HelloServiceGrpc.newStub(channel);

        CountDownLatch finishLatch = new CountDownLatch(1);

        StreamObserver<HelloResponse> responseObserver = new StreamObserver<HelloResponse>() {
            @Override
            public void onNext(HelloResponse response) {
                System.out.println("Ответ от сервера: " + response.getGreeting());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Сервер завершил соединение.");
                finishLatch.countDown();
            }
        };

        StreamObserver<HelloRequest> requestObserver = stub.clientStreamHello(responseObserver);

        // Отправляем несколько запросов
        requestObserver.onNext(HelloRequest.newBuilder().setFirstName("Anna").setLastName("Ivanova").build());
        requestObserver.onNext(HelloRequest.newBuilder().setFirstName("Ivan").setLastName("Petrov").build());
        requestObserver.onNext(HelloRequest.newBuilder().setFirstName("John").setLastName("Doe").build());

        requestObserver.onCompleted(); // Завершаем поток запросов

        finishLatch.await(5, TimeUnit.SECONDS);
        channel.shutdown();
    }
}
