package org.baeldung.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder
                .forPort(8085)
                .addService(new HelloServiceImpl())
                .build();

        System.out.println("gRPC run to port 8085");
        server.start();
        server.awaitTermination();
    }
}
