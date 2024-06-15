package application;

import static spark.Spark.*;

import java.util.UUID;

import com.google.gson.Gson;
import services.WalletService;
import models.Wallet;

public class WalletApplication {

    private final WalletService walletService;
    private final Gson gson = new Gson();

    public WalletApplication(WalletService walletService) {
        this.walletService = walletService;
    }

    public void initializeRoutes() {
        // before((request, response) -> {
        //     response.header("Access-Control-Allow-Origin", "*");
        //     response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        //     response.header("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
        //     response.header("Access-Control-Allow-Credentials", "true");
        // });

        // options("/*", (request, response) -> {
        //     String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
        //     if (accessControlRequestHeaders != null) {
        //         response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
        //     }

        //     String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
        //     if (accessControlRequestMethod != null) {
        //         response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
        //     }

        //     return "OK";
        // });

//        post("/wallets", (req, res) -> {
//            res.type("application/json");
//            UUID userId = UUID.randomUUID();
//            UUID walletId = UUID.randomUUID();
//            walletService.createWallet(userId);
//            return gson.toJson(new StandardResponse(StatusResponse.SUCCESS, "Carteira criada com sucesso."));
//        });

        get("/wallets/:userId", (req, res) -> {
            res.type("application/json");
            UUID userId = UUID.fromString(req.params(":userId"));
            Wallet wallet = walletService.getWalletByUserId(userId);

            if (wallet != null) {
                return gson.toJson(new StandardResponse(StatusResponse.SUCCESS, gson.toJsonTree(wallet)));
            } else {
                res.status(404);
                return gson.toJson(new StandardResponse(StatusResponse.ERROR, "Carteira não encontrada."));
            }
        });

        post("/wallets/deposit/fromUser/:fromUser/toUser/:toUser", (req, res) -> {
            System.out.println(" oi oi oio io i oioi");
            res.type("application/json");
            UUID fromUser = UUID.fromString(req.params(":fromUser"));
            UUID toUser = UUID.fromString(req.params(":toUser"));
            String name = req.queryParams("name");
            double amount = Double.parseDouble(req.queryParams("amount"));
            try {
                walletService.deposit(fromUser, toUser, name, amount);
                return gson.toJson(new StandardResponse(StatusResponse.SUCCESS, "Depósito realizado com sucesso."));
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(new StandardResponse(StatusResponse.ERROR, "Erro ao realizar depósito: " + e.getMessage()));
            }
        });

        post("/wallets/withdraw/:userId", (req, res) -> {
            res.type("application/json");
            UUID userId = UUID.fromString(req.params(":userId"));
            String currency = req.queryParams("currency");
            double amount = Double.parseDouble(req.queryParams("amount"));
            try {
                walletService.withdraw(userId, currency, amount);
                return gson.toJson(new StandardResponse(StatusResponse.SUCCESS, "Saque realizado com sucesso."));
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(new StandardResponse(StatusResponse.ERROR, "Erro ao realizar saque: " + e.getMessage()));
            }
        });
        post("/wallets/:fromUserId/transfer", (req, res) -> {
            res.type("application/json");
            UUID fromUserId = UUID.fromString(req.params(":fromUserId"));
            UUID toUserId = UUID.fromString(req.queryParams("toUserId"));
            String currency = req.queryParams("currency");
            double amount = Double.parseDouble(req.queryParams("amount"));
            try {
                walletService.transfer(fromUserId, toUserId, currency, amount);
                return gson.toJson(new StandardResponse(StatusResponse.SUCCESS, "Transferência realizada com sucesso."));
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(new StandardResponse(StatusResponse.ERROR, "Erro ao realizar transferência: " + e.getMessage()));
            }
        });
    }
}

