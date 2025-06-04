function openTab(tabName, event) {
    // Esconde todos os conteúdos de tab
    const tabContents = document.getElementsByClassName('tab-content');
    for (let i = 0; i < tabContents.length; i++) {
        tabContents[i].style.display = 'none';
    }

    // Remove a classe 'active' de todos os botões
    const tabButtons = document.getElementsByClassName('tab-button');
    for (let i = 0; i < tabButtons.length; i++) {
        tabButtons[i].classList.remove('active');
    }

    // Mostra a tab atual e marca o botão como ativo
    document.getElementById(tabName).style.display = 'block';
    if (event && event.currentTarget) {
        event.currentTarget.classList.add('active');
    }
}


// Cadastro de remédio
document.getElementById('formCadastro').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const remedio = {
        nome: document.getElementById('nome').value,
        via: document.getElementById('via').value,
        lote: document.getElementById('lote').value,
        quantidade: parseInt(document.getElementById('quantidade').value),
        validade: document.getElementById('validade').value,
        laboratorio: document.getElementById('laboratorio').value
    };

    if (!remedio.nome || !remedio.via || !remedio.lote || isNaN(remedio.quantidade) || !remedio.validade || !remedio.laboratorio) {
        document.getElementById('cadastroResult').innerHTML = `
            <div class="error">
                <p>Preencha todos os campos obrigatórios corretamente.</p>
            </div>
        `;
        return;
    }
    
    try {
        const response = await fetch('/api/remedios', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(remedio)
        });
        
        const data = await response.json();
        
        document.getElementById('cadastroResult').innerHTML = `
            <div class="success">
                <p>Remédio cadastrado com sucesso!</p>
                
            </div>
        `;
        
        // Limpa o formulário
        document.getElementById('formCadastro').reset();
    } catch (error) {
        document.getElementById('cadastroResult').innerHTML = `
            <div class="error">
                <p>Erro ao cadastrar remédio:</p>
                <p>${error.message}</p>
            </div>
        `;
    }
});

// Listagem de remédios
async function carregarRemedios() {
    try {
        const response = await fetch('/api/remedios');
        const remedios = await response.json();
        
        const listaDiv = document.getElementById('listaRemedios');
        if (remedios.length === 0) {
            listaDiv.innerHTML = '<p>Nenhum remédio cadastrado.</p>';
            return;
        }
        
        let html = '<div class="remedio-list">';
        remedios.forEach(remedio => {
            html += `
                <div class="remedio-item">
                    <span>${remedio.id} - ${remedio.nome}  - Quantidade:${remedio.quantidade}</span>
                    <button onclick="mostrarDetalhes(${remedio.id})">Detalhes</button>
                </div>
            `;
        });
        html += '</div>';
        
        listaDiv.innerHTML = html;
    } catch (error) {
        document.getElementById('listaRemedios').innerHTML = `
            <div class="error">
                <p>Erro ao carregar remédios:</p>
                <p>${error.message}</p>
            </div>
        `;
    }
}


// Função para mostrar detalhes a partir da lista
async function mostrarDetalhes(id) {
    document.getElementById('idDetalhe').value = id;
    openTab('detalhar');
    buscarDetalhes();
}

async function buscarDetalhes() {
    const id = document.getElementById('idDetalhe').value;
    if (!id) return;
    
    try {
        const response = await fetch(`/api/remedios/${id}`);
        const remedio = await response.json();

      document.getElementById('detalhesRemedio').innerHTML = `
        <div class="remedio-detalhes">
            <h3>${remedio.nome}</h3>
            <p><strong>ID:</strong> ${remedio.id}</p>
            <p><strong>Via:</strong> ${remedio.via}</p>
            <p><strong>Lote:</strong> ${remedio.lote}</p>
            <p><strong>Quantidade:</strong> ${remedio.quantidade}</p>
            <p><strong>Validade:</strong> ${remedio.validade}</p>
            <p><strong>Laboratório:</strong> ${remedio.laboratorio}</p>
            <p><strong>Status:</strong> ${remedio.ativo ? 'Ativo' : 'Inativo'}</p>
        </div>
        <button id="btnLiberar">Dar Baixa</button>
        <button id="btnEditar">Atualizar</button>
        <button id="btnHistorico">Historico</button>
        <div id="formLiberarContainer" style="display:none;"></div>
        <div id="formAlterarContainer" style="display:none;"></div>
        <div id="HistoricoContainer" style="display:none;"></div>
        
        `;
        document.getElementById('btnEditar').addEventListener('click', () => {
            mostrarFormularioAlterar(remedio);
        });
         document.getElementById('btnLiberar').addEventListener('click', () => {
            mostrarFormularioLiberar(remedio);
        });
        document.getElementById('btnHistorico').addEventListener('click', () => {
            mostrarHistorico(remedio);
        });

    } catch (error) {
        document.getElementById('detalhesRemedio').innerHTML = `
            <div class="error">
                <p>Erro ao buscar remédio:</p>
                <p>${error.message}</p>
            </div>
        `;
    }
}

async function mostrarHistorico(remedio) {
    try {
        const response = await fetch(`/api/remedios/${remedio.id}/historico`);
        const json = await response.json();
        const historico = Array.isArray(json) ? json : json.historicos;

        console.log('Histórico recebido:', historico);

        const container = document.getElementById('HistoricoContainer');
        container.style.display = 'block';

        let html = '<h4>Histórico do Remédio</h4>';
        if (historico && historico.length > 0) {
            html += '<ul>';
            historico.forEach(entry => {
                console.log('Entrada no histórico:', entry);
                html += `<li>${new Date(entry.dataAlteracao).toLocaleString()} - ${entry.tipoAlteracao}</li>`;
            });
            html += '</ul>';
        } else {
            html += '<p>Sem histórico disponível.</p>';
        }

        container.innerHTML = html;

    } catch (error) {
        document.getElementById('HistoricoContainer').innerHTML = `
            <div class="error">
                <p>Erro ao carregar histórico:</p>
                <p>${error.message}</p>
            </div>
        `;
    }
}

function mostrarFormularioLiberar(remedio) {
  const container = document.getElementById('formLiberarContainer'); // <- alterar para o container certo
  
  container.style.display = 'block';
  container.innerHTML = `
    <h2>Dar Baixa Medicamento</h2>
    <form id="formBaixaEstoque">
      <input type="hidden" id="darBaixaId" value="${remedio.id}">  
      <label>Quantidade a dar baixa:
        <input type="number" id="quantidadeBaixa" min="1" required>
      </label>
      <button type="submit">Confirmar Baixa</button>
      <button type="button" id="cancelarBaixa">Cancelar</button>
    </form>
    <div id="baixaResult"></div>
  `;

  document.getElementById('formBaixaEstoque').addEventListener('submit', darBaixaEstoque);

  document.getElementById('cancelarBaixa').addEventListener('click', () => {
    container.style.display = 'none';
    container.innerHTML = '';  // opcional: limpa o conteúdo
  });
}

function mostrarFormularioAlterar(remedio) {
  const container = document.getElementById('formAlterarContainer');
  
  container.style.display = 'block';
  container.innerHTML = `
    <h4>Alterar Medicamento</h4>
    <form id="formAlterar">
      <input type="hidden" id="alterarId" value="${remedio.id}">
      <label>Nome: <input type="text" id="alterarNome" value="${remedio.nome}"></label><br>
      <label>Via: <input type="text" id="alterarVia" value="${remedio.via}"></label><br>
      <label>Lote: <input type="text" id="alterarLote" value="${remedio.lote}"></label><br>
      <label>Quantidade: <input type="number" id="alterarQuantidade" value="${remedio.quantidade}"></label><br>
      <label>Validade: <input type="date" id="alterarValidade" value="${remedio.validade}"></label><br>
      <label>Laboratório: <input type="text" id="alterarLaboratorio" value="${remedio.laboratorio}"></label><br>
      <button type="submit">Salvar Alterações</button>
      <button type="button" id="cancelarAlterar">Cancelar</button>
    </form>
    <div id="alterarResult"></div>
  `;

  document.getElementById('formAlterar').addEventListener('submit', alterarRemedio);
  document.getElementById('cancelarAlterar').addEventListener('click', () => {
    container.style.display = 'none';
    container.innerHTML = '';  // opcional: limpa o conteúdo para evitar conflitos futuros
  });

}

async function darBaixaEstoque(e) {
    e.preventDefault(); // para prevenir o submit padrão

    const quantidade = parseInt(document.getElementById('quantidadeBaixa').value);
    const id = parseInt(document.getElementById('darBaixaId').value);


    if (!id) {
        document.getElementById('baixaResult').innerHTML = `
            <div class="error">
                <p>ID do remédio não encontrado.</p>
            </div>
        `;
        return;
    }

    try {
        const response = await fetch(`/api/remedios/${id}/baixa`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ quantidade })
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Erro ao realizar baixa.');
        }

        const data = await response.json();

        document.getElementById('baixaResult').innerHTML = `
            <div class="success">
                <p>Baixa realizada com sucesso!</p>
                <p>Nova quantidade: ${data.quantidade}</p>
            </div>
        `;

        buscarDetalhes(); // Atualiza os detalhes após a baixa
    } catch (error) {
        document.getElementById('baixaResult').innerHTML = `
            <div class="error">
                <p>Erro na baixa de estoque:</p>
                <p>${error.message}</p>
            </div>
        `;
    }
}



async function alterarRemedio(e) {
    e.preventDefault();

    const id = document.getElementById('alterarId').value;

    const remedioAtualizado = {
        id: parseInt(id),
        nome: document.getElementById('alterarNome').value,
        via: document.getElementById('alterarVia').value,
        lote: document.getElementById('alterarLote').value,
        quantidade: parseInt(document.getElementById('alterarQuantidade').value),
        validade: document.getElementById('alterarValidade').value,
        laboratorio: document.getElementById('alterarLaboratorio').value
    };

    try {
        const response = await fetch(`/api/remedios/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(remedioAtualizado)
        });

        const data = await response.json();

        document.getElementById('alterarResult').innerHTML = `
            <div class="success">
                <p>Remédio atualizado com sucesso!</p>
                <pre>${JSON.stringify(data, null, 2)}</pre>
            </div>
        `;

        buscarDetalhes(); // Recarrega os detalhes atualizados
    } catch (error) {
        document.getElementById('alterarResult').innerHTML = `
            <div class="error">
                <p>Erro ao atualizar remédio:</p>
                <p>${error.message}</p>
            </div>
        `;
    }
}
