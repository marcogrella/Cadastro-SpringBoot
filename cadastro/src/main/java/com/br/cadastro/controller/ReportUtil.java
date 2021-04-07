package com.br.cadastro.controller;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletContext;
import org.springframework.stereotype.Component;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

// classe criada para gerar relatório. Pasta está em src>main>java>webapp>relatorios e utiliza o Report

@Component
public class ReportUtil implements Serializable{

	private static final long serialVersionUID = 1L;

	// retorna o pdf em byte para fazer o download no navegador
	// A lista pode ser qualquer uma, pois o método aceita qualquer uma. 
	public byte[] gerarRelatorio(List listDados, String relatorio, ServletContext servletContext) throws Exception{
		
		
		// cria a lista de dados para o relatório com a lista de objetos para impressão
		 JRBeanCollectionDataSource jrbcds = new JRBeanCollectionDataSource(listDados);
		 
		 // carrega o caminho do arquivo jasper que está compilado:
		 String caminhoJasper = servletContext.getRealPath("relatorios") + File.separator + relatorio + ".jasper";
		 
		 // carrregar o arquivo jasper passando os dados:
		 JasperPrint impressoraJasper = JasperFillManager.fillReport(caminhoJasper, new HashMap(), jrbcds);
		
		 // exporta para byte[] com o intuito para baixar o PDF. 
		return JasperExportManager.exportReportToPdf(impressoraJasper);
	}
	
	
}
