package com.example.materabank.infra.controller;

import com.example.materabank.application.AccountUseCase;
import com.example.materabank.infra.controller.dto.request.AccountRequestDTO;
import com.example.materabank.infra.controller.dto.request.TransactionListRequest;
import com.example.materabank.infra.controller.dto.response.AccountResponseDTO;
import com.example.materabank.infra.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountUseCase accountUseCase;
    private final TokenService tokenService;

    @Operation(summary = "Cria uma nova conta bancária")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conta criada com sucesso",
                    content = @Content(schema = @Schema(implementation = AccountResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF já possui conta", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody @Valid AccountRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountUseCase.create(dto));
    }

    @Operation(summary = "Lista todas as contas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contas listadas com sucesso",
                    content = @Content(schema = @Schema(implementation = AccountResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @GetMapping("/all-accounts")
    public ResponseEntity<List<AccountResponseDTO>> listAll() {
        return ResponseEntity.ok(accountUseCase.listAll());
    }

    @Operation(summary = "Busca uma conta por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta encontrada com sucesso",
                    content = @Content(schema = @Schema(implementation = AccountResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(accountUseCase.findById(id));
    }

    @Operation(summary = "Exclui uma conta pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conta excluída com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        val loggedUser = tokenService.getLoggedUser();
        val account = accountUseCase.findById(id);

        if (!account.userId().equals(loggedUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        accountUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Consulta o saldo atual de uma conta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo obtido com sucesso",
                    content = @Content(schema = @Schema(implementation = BigDecimal.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long id) {
        return ResponseEntity.ok(accountUseCase.findById(id).balance());
    }

    @PostMapping("/{id}/transactions")
    @Operation(
            summary = "Realiza múltiplos lançamentos de crédito e débito",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TransactionListRequest.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de múltiplas transações",
                                    value = """
                                        {
                                          "transactions": [
                                            { "type": "CREDIT", "amount": 100.00 },
                                            { "type": "DEBIT", "amount": 50.00 }
                                          ]
                                        }
                                        """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lançamentos realizados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou saldo insuficiente"),
            @ApiResponse(responseCode = "403", description = "Usuário não autorizado para essa conta"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Void> processTransactions(
            @PathVariable Long id,
            @RequestBody @Valid TransactionListRequest request) {

        val loggedUser = tokenService.getLoggedUser();
        val account = accountUseCase.findById(id);

        if (!account.userId().equals(loggedUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        accountUseCase.processTransactions(id, request.transactions());
        return ResponseEntity.ok().build();
    }
}
