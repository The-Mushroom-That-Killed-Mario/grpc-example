package org.baeldung.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.baeldung.grpc.HelloRequest;
import org.baeldung.grpc.HelloResponse;
import org.baeldung.grpc.HelloServiceGrpc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GrpcBidirectionalClient {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8085)
                .usePlaintext()
                .build();

        HelloServiceGrpc.HelloServiceStub stub = HelloServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<HelloResponse> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(HelloResponse response) {
                System.out.println("Server responded: " + response.getGreeting());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Client error: " + t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Chat ended by server.");
                latch.countDown();
            }
        };

        StreamObserver<HelloRequest> requestObserver = stub.chatHello(responseObserver);

        // Simulating sending multiple messages
        requestObserver.onNext(HelloRequest.newBuilder().setFirstName("Elena").setLastName("Smirnova").build());
        Thread.sleep(500);

        requestObserver.onNext(HelloRequest.newBuilder().setFirstName("Alex").setLastName("Kozlov").build());
        Thread.sleep(500);

        requestObserver.onNext(HelloRequest.newBuilder().setFirstName("Mia").setLastName("Chen").build());

        // Ending the chat
        requestObserver.onCompleted();

        latch.await(5, TimeUnit.SECONDS);
        channel.shutdown();
    }
}
