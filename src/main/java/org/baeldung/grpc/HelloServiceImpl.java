package org.baeldung.grpc;

import io.grpc.stub.StreamObserver;

public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

    // no streaming (Blocking stub)
    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        String greeting = "Hello, " + request.getFirstName() + " " + request.getLastName();
        HelloResponse response = HelloResponse.newBuilder().setGreeting(greeting).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Server streaming
    @Override
    public void streamHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        for (int i = 1; i <= 5; i++) {
            String greeting = "Hello #" + i + " to " + request.getFirstName() + " " + request.getLastName();
            HelloResponse response = HelloResponse.newBuilder()
                    .setGreeting(greeting)
                    .build();

            responseObserver.onNext(response);

            try {
                Thread.sleep(1000); // задержка между сообщениями
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        responseObserver.onCompleted();
    }

    // Client streaming
    @Override
    public StreamObserver<HelloRequest> clientStreamHello(StreamObserver<HelloResponse> responseObserver) {
        return new StreamObserver<HelloRequest>() {

            final StringBuilder allNames = new StringBuilder();

            @Override
            public void onNext(HelloRequest request) {

                System.out.println("Client sent request:");
                System.out.println(request);

                allNames.append(request.getFirstName())
                        .append(" ")
                        .append(request.getLastName())
                        .append("; ");
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                String result = "Hello to everyone: " + allNames;
                HelloResponse response = HelloResponse.newBuilder()
                        .setGreeting(result)
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

    // Bidirectional streaming
    @Override
    public StreamObserver<HelloRequest> chatHello(StreamObserver<HelloResponse> responseObserver) {
        return new StreamObserver<HelloRequest>() {

            @Override
            public void onNext(HelloRequest request) {
                String reply = "👋 Hello, " + request.getFirstName() + " " + request.getLastName();
                HelloResponse response = HelloResponse.newBuilder()
                        .setGreeting(reply)
                        .build();

                responseObserver.onNext(response); // Отвечаем сразу
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Server error in chatHello: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Server is closing the chat.");
                responseObserver.onCompleted();
            }
        };
    }

}
