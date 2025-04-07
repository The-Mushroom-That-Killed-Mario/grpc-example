package org.baeldung.grpc;

import io.grpc.stub.StreamObserver;

public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

    //no streaming (Blocking stub)
    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        String greeting = "Hello, " + request.getFirstName() + " " + request.getLastName();
        HelloResponse response = HelloResponse.newBuilder().setGreeting(greeting).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    //Server streaming
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

    //Client streaming
    @Override
    public StreamObserver<HelloRequest> clientStreamHello(StreamObserver<HelloResponse> responseObserver) {
        return new StreamObserver<HelloRequest>() {

            StringBuilder allNames = new StringBuilder();

            @Override
            public void onNext(HelloRequest request) {

                System.out.println("Клиент прислал request:");
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
                String result = "Привет всем: " + allNames.toString();
                HelloResponse response = HelloResponse.newBuilder()
                        .setGreeting(result)
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

}
