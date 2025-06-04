package com.example.servidor.remedio;

import com.example.servidor.Empty;
import com.example.servidor.IdRequest;
import com.example.servidor.ListaHistoricoAlteracoes;
import com.example.servidor.ListaRemedios;
import com.example.servidor.RemedioServiceGrpc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.servidor.DadosListagemRemedio;
import com.example.servidor.DadosDetalhamentoRemedio;
import com.example.servidor.DadosHistoricoAlteracao;
import com.example.servidor.DadosCadastroRemedio;
import com.example.servidor.BaixaEstoqueRequest;
import com.example.servidor.DadosAtualizarRemedio;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class RemedioServiceImpl extends RemedioServiceGrpc.RemedioServiceImplBase {
	@Autowired
	private RemedioRepository repository;
	@Autowired
	private HistoricoAlteracaoRepository historicoAlteracaoRepository;
	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void cadastrar(DadosCadastroRemedio request, StreamObserver<DadosDetalhamentoRemedio> responseObserver) {
		try {
			Laboratorio laboratorio = Laboratorio.valueOf(request.getLaboratorio());
			Via via = Via.valueOf(request.getVia());
			LocalDate validade = LocalDate.parse(request.getValidade());

			if (request.getNome().isEmpty() || request.getLote().isEmpty()) {
				throw new IllegalArgumentException("Nome e Lote são obrigatórios.");
			}

			if (request.getQuantidade() < 0) {
				throw new IllegalArgumentException("Quantidade deve ser positiva.");
			}

			Remedio remedio = new Remedio(0, request.getNome(), via, request.getLote(),
					request.getQuantidade(), validade, laboratorio, true);

			repository.save(remedio);

			DadosDetalhamentoRemedio response = DadosDetalhamentoRemedio.newBuilder()
					.setId(remedio.getId())
					.setNome(remedio.getNome())
					.setVia(remedio.getVia().toString())
					.setLote(remedio.getLote())
					.setLaboratorio(remedio.getLaboratorio().toString())
					.setQuantidade(remedio.getQuantidade())
					.setAtivo(remedio.isAtivo())
					.setValidade(remedio.getValidade().toString())
					.build();

			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (IllegalArgumentException e) {
			responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		} catch (Exception e) {
			responseObserver.onError(io.grpc.Status.INTERNAL.withDescription("Erro ao cadastrar remédio.").withCause(e).asRuntimeException());
		}
	}


	@Override
	public void listar(Empty request, StreamObserver<ListaRemedios> responseObserver) {
		ListaRemedios.Builder builder = ListaRemedios.newBuilder();
		repository.findAllByAtivoTrue().forEach(remedio -> {
			builder.addRemedios(
					DadosListagemRemedio.newBuilder().setId(remedio.getId()).setNome(remedio.getNome()).setQuantidade(remedio.getQuantidade()).build());
		});
		responseObserver.onNext(builder.build());
		responseObserver.onCompleted();
	}
	
	@Override
	@Transactional
	public void atualizar(DadosAtualizarRemedio request, StreamObserver<DadosDetalhamentoRemedio> responseObserver) {
	    Remedio remedio = repository.getReferenceById(request.getId());
	    
	    // Serializa o estado antigo (antes da alteração)
	    String estadoAntigo = serializeRemedio(remedio);
	    
	    remedio.setNome(request.getNome());
	    remedio.setVia(Via.valueOf(request.getVia()));
	    remedio.setLote(request.getLote());
	    remedio.setQuantidade(request.getQuantidade());
	    remedio.setValidade(LocalDate.parse(request.getValidade()));
	    remedio.setLaboratorio(Laboratorio.valueOf(request.getLaboratorio()));

	    repository.save(remedio);
	    // Serializa o estado novo (após alteração)
	    String estadoNovo = serializeRemedio(remedio);
	    
	   // Salva histórico agrupado
	    historicoAlteracaoRepository.save(new HistoricoAlteracao(remedio.getId(), estadoAntigo, estadoNovo, "ATUALIZACAO"));

	    DadosDetalhamentoRemedio response = DadosDetalhamentoRemedio.newBuilder()
	            .setId(remedio.getId())
	            .setNome(remedio.getNome())
	            .setLaboratorio(remedio.getLaboratorio().toString())
	            .setVia(remedio.getVia().toString())
	            .setLote(remedio.getLote())
	            .setQuantidade(remedio.getQuantidade())
	            .setValidade(remedio.getValidade().toString())
	            .setAtivo(remedio.isAtivo())
	            .build();

	    responseObserver.onNext(response);
	    responseObserver.onCompleted();
	}

	@Override
	public void deletar(IdRequest request, StreamObserver<Empty> responseObserver) {
		repository.deleteById(request.getId());
		responseObserver.onNext(Empty.newBuilder().build());
		responseObserver.onCompleted();
	}

	@Override
	@Transactional
	public void desativar(IdRequest request, StreamObserver<Empty> responseObserver) {
		Remedio remedio = repository.getReferenceById(request.getId());
		remedio.desativar();
		responseObserver.onNext(Empty.newBuilder().build());
		responseObserver.onCompleted();
	}

	@Override
	@Transactional
	public void ativar(IdRequest request, StreamObserver<Empty> responseObserver) {
		Remedio remedio = repository.getReferenceById(request.getId());
		remedio.ativar();
		responseObserver.onNext(Empty.newBuilder().build());
		responseObserver.onCompleted();
	}

	@Override
	@Transactional
	public void detalhar(IdRequest request, StreamObserver<DadosDetalhamentoRemedio> responseObserver) {
		Remedio remedio = repository.getReferenceById(request.getId());
		DadosDetalhamentoRemedio response = DadosDetalhamentoRemedio.newBuilder().setId(remedio.getId())
				.setNome(remedio.getNome()).setVia(remedio.getVia().toString()).setLote(remedio.getLote()).setLaboratorio(remedio.getLaboratorio().toString())
				.setQuantidade(remedio.getQuantidade()).setValidade(remedio.getValidade().toString()).setAtivo(remedio.isAtivo()).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
	
	@Override
	public void consultarHistorico(IdRequest request, StreamObserver<ListaHistoricoAlteracoes> responseObserver) {
	    Long remedioId = request.getId();

	    // Consulta os históricos do remedio no repositório
	    List<HistoricoAlteracao> historicos = historicoAlteracaoRepository.findByRemedioId(remedioId);

	    ListaHistoricoAlteracoes.Builder builder = ListaHistoricoAlteracoes.newBuilder();

	    for (HistoricoAlteracao historico : historicos) {
	        DadosHistoricoAlteracao dados = DadosHistoricoAlteracao.newBuilder()
	            .setRemedioId(historico.getRemedioId())
	            .setEstadoAntigo(historico.getEstadoAntigo())
	            .setEstadoNovo(historico.getEstadoNovo())
	            .setTipoAlteracao(historico.getTipoAlteracao())
	            .setDataAlteracao(historico.getDataAlteracao().toString()) // se existir data
	            .build();
	        builder.addHistoricos(dados);
	    }

	    responseObserver.onNext(builder.build());
	    responseObserver.onCompleted();
	}

	@Override
	@Transactional
	public void darBaixaEstoque(BaixaEstoqueRequest request, StreamObserver<DadosDetalhamentoRemedio> responseObserver) {
		try {
			Remedio remedio = repository.getReferenceById(request.getId());
			String estadoAntigo = serializeRemedio(remedio);

			int novaQuantidade = remedio.getQuantidade() - request.getQuantidade();
			if (novaQuantidade < 0) {
				throw new IllegalArgumentException("Estoque insuficiente para a baixa solicitada.");
			}

			remedio.setQuantidade(novaQuantidade);
			repository.save(remedio);

			String estadoNovo = serializeRemedio(remedio);
			historicoAlteracaoRepository.save(new HistoricoAlteracao(remedio.getId(), estadoAntigo, estadoNovo, "BAIXA_ESTOQUE"));

			DadosDetalhamentoRemedio response = DadosDetalhamentoRemedio.newBuilder()
					.setId(remedio.getId())
					.setNome(remedio.getNome())
					.setVia(remedio.getVia().toString())
					.setLote(remedio.getLote())
					.setLaboratorio(remedio.getLaboratorio().toString())
					.setQuantidade(remedio.getQuantidade())
					.setValidade(remedio.getValidade().toString())
					.setAtivo(remedio.isAtivo())
					.build();

			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (IllegalArgumentException e) {
			responseObserver.onError(io.grpc.Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());
		} catch (Exception e) {
			responseObserver.onError(io.grpc.Status.INTERNAL.withDescription("Erro ao dar baixa no estoque.").withCause(e).asRuntimeException());
		}
	}

	private String serializeRemedio(Remedio remedio) {
	    try {
	        return objectMapper.writeValueAsString(remedio);
	    } catch (Exception e) {
	        // fallback simples
	        return String.format("id=%d, nome=%s, via=%s, lote=%s, quantidade=%d, validade=%s, laboratorio=%s, ativo=%s",
	                remedio.getId(), remedio.getNome(), remedio.getVia(), remedio.getLote(),
	                remedio.getQuantidade(), remedio.getValidade(), remedio.getLaboratorio(), remedio.isAtivo());
	    }
	}

	private RuntimeException grpcError(io.grpc.Status status, String message, Throwable cause) {
		return status.withDescription(message).withCause(cause).asRuntimeException();
	}

}
