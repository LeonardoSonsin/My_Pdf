package com.example.mypdf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class PDF {
    static int eixoY;
    static int eixoYTexto;
    static int incrementaY;

    static int numeroPagina;

    static Paint paintLinhas;
    static Paint paintTitulo;
    static Paint paintTexto;
    static Paint paintTextoNegrito;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void gerarPdf(Context context) {
        PdfDocument pdfDocument = new PdfDocument();
        configurarPaintPdf();
        PdfDocument.Page pagina = gerarPrimeiraPagina(pdfDocument);
        Canvas canvas = pagina.getCanvas();
        iniciarOrientacaoPagina();
        pagina = desenharPDF(pdfDocument, pagina, canvas);
        pdfDocument.finishPage(pagina);
        salvarPDF(context, pdfDocument);
        pdfDocument.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static PdfDocument.Page desenharPDF(PdfDocument pdfDocument, PdfDocument.Page pagina, Canvas canvas) {
        desenharLogoEmpresa(pagina, canvas);

        desenharCabecalho(canvas, pagina);

        eixoY = incrementaY;
        eixoYTexto = incrementaY + 26;
        desenharTabelaProdutos(pagina, canvas, "ELEMENTOS QUE COMPÕEM O PEDIDO");

        for (int i = 0; i < 10; i++) {
            desenharListaProdutos(canvas);
            if (incrementaY >= 1150) {
                numeroPagina = finalizarPagina(pdfDocument, numeroPagina, pagina);
                pagina = iniciarNovaPagina(pdfDocument, numeroPagina);
                canvas = pagina.getCanvas();
                iniciarOrientacaoPagina();
            }
        }
        desenharTotalPedido(canvas, "TOTAL PEDIDO:", 640);

        incrementaY += 80;
        eixoY = incrementaY;
        eixoYTexto = incrementaY + 26;
        if (incrementaY > 960) {
            numeroPagina = finalizarPagina(pdfDocument, numeroPagina, pagina);
            pagina = iniciarNovaPagina(pdfDocument, numeroPagina);
            canvas = pagina.getCanvas();
            iniciarOrientacaoPagina();
        }
        desenharTabelaProdutos(pagina, canvas, "BONIFICAÇÃO");
        for (int i = 0; i < 10; i++) {
            desenharListaProdutos(canvas);
            if (incrementaY >= 1150) {
                numeroPagina = finalizarPagina(pdfDocument, numeroPagina, pagina);
                pagina = iniciarNovaPagina(pdfDocument, numeroPagina);
                canvas = pagina.getCanvas();
                iniciarOrientacaoPagina();
            }
        }
        desenharTotalPedido(canvas, "TOTAL BONIFICADO:", 626);

        incrementaY += 80;
        eixoY = incrementaY;
        eixoYTexto = incrementaY + 26;
        if (incrementaY > 960) {
            numeroPagina = finalizarPagina(pdfDocument, numeroPagina, pagina);
            pagina = iniciarNovaPagina(pdfDocument, numeroPagina);
            canvas = pagina.getCanvas();
            iniciarOrientacaoPagina();
        }
        desenharRodape(canvas);
        desenharMensagemRodape(pagina, canvas);

        return pagina;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void desenharLogoEmpresa(PdfDocument.Page pagina, Canvas canvas) {
        try {
            Bitmap imagem = redimensionarImagem(R.drawable.blink);
            int posicao_imagem = (pagina.getInfo().getPageWidth() - imagem.getWidth()) / 2;
            canvas.drawBitmap(imagem, posicao_imagem, 50, paintLinhas);
            eixoY += imagem.getHeight() + 40;
            eixoYTexto += imagem.getHeight() + 40;
        } catch (Exception ignored) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void desenharCabecalho(Canvas canvas, PdfDocument.Page pagina) {
        // Linhas Horizontais
        incrementaY = eixoY;
        for (int i = 0; i < 8; i++) {
            canvas.drawLine(30, incrementaY, 870, incrementaY, paintLinhas);
            incrementaY += 40;
        }
        // Linhas Verticais
        canvas.drawLine(30, eixoY, 30, incrementaY - 40, paintLinhas);
        canvas.drawLine(870, eixoY, 870, incrementaY - 40, paintLinhas);
        canvas.drawLine(550, eixoY + 40, 550, incrementaY - 160, paintLinhas);
        canvas.drawLine(550, incrementaY - 120, 550, incrementaY - 80, paintLinhas);

        // Texto
        canvas.drawText("DADOS DA EMPRESA E DO CLIENTE", pagina.getInfo().getPageWidth() / 2, eixoYTexto, paintTitulo);
        canvas.drawText("RAZÃO SOCIAL DA EMPRESA", 40, eixoYTexto + 40, paintTexto);
        canvas.drawText("Pedido Nº", 674, eixoYTexto + 40, paintTexto);
        canvas.drawText("123456789", 760, eixoYTexto + 40, paintTextoNegrito);
        canvas.drawText("RUA DA EMPRESA, 123 - BAIRRO", 40, eixoYTexto + 80, paintTexto);
        canvas.drawText("Data Emissão:", 651, eixoYTexto + 80, paintTexto);
        canvas.drawText("12/07/2022 09:52", 741, eixoYTexto + 80, paintTextoNegrito);
        canvas.drawText("CEP: 12.345-000 - CIDADE/UF", 40, eixoYTexto + 120, paintTexto);
        canvas.drawText("Data Conclusão:", 640, eixoYTexto + 120, paintTexto);
        canvas.drawText("12/07/2022 10:12", 740, eixoYTexto + 120, paintTextoNegrito);
        canvas.drawText("Telefone:", 40, eixoYTexto + 160, paintTexto);
        canvas.drawText("(14)1234-5678", 95, eixoYTexto + 160, paintTextoNegrito);
        canvas.drawText("Cliente:", 40, eixoYTexto + 200, paintTexto);
        canvas.drawText("RAZÃO SOCIAL DO CLIENTE", 85, eixoYTexto + 200, paintTextoNegrito);
        canvas.drawText("CNPJ:", 695, eixoYTexto + 200, paintTexto);
        canvas.drawText("01.234.567/0001-89", 744, eixoYTexto + 200, paintTextoNegrito);
        canvas.drawText("RUA DO CLIENTE, 1234 - BAIRRO, CIDADE - UF, CEP", 40, eixoYTexto + 240, paintTexto);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void desenharTabelaProdutos(PdfDocument.Page pagina, Canvas canvas, String titulo) {
        // Linhas Horizontais
        for (int i = 0; i < 3; i++) {
            canvas.drawLine(30, incrementaY, 870, incrementaY, paintLinhas);
            incrementaY += 40;
        }

        // Linhas Verticais
        canvas.drawLine(30, eixoY, 30, incrementaY - 40, paintLinhas);
        canvas.drawLine(870, eixoY, 870, incrementaY - 40, paintLinhas);
        canvas.drawLine(150, eixoY + 40, 150, incrementaY - 40, paintLinhas);
        canvas.drawLine(570, eixoY + 40, 570, incrementaY - 40, paintLinhas);
        canvas.drawLine(620, eixoY + 40, 620, incrementaY - 40, paintLinhas);
        canvas.drawLine(740, eixoY + 40, 740, incrementaY - 40, paintLinhas);

        // Texto
        canvas.drawText(titulo, pagina.getInfo().getPageWidth() >> 1, eixoYTexto, paintTitulo);
        canvas.drawText("Código", 40, eixoYTexto + 40, paintTextoNegrito);
        canvas.drawText("Descrição", 160, eixoYTexto + 40, paintTextoNegrito);
        canvas.drawText("Qtd.", 585, eixoYTexto + 40, paintTextoNegrito);
        canvas.drawText("Valor", 680, eixoYTexto + 40, paintTextoNegrito);
        canvas.drawText("Total", 810, eixoYTexto + 40, paintTextoNegrito);

        incrementaY -= 40;
    }

    private static void desenharListaProdutos(Canvas canvas) {
        eixoY = incrementaY;
        eixoYTexto = incrementaY + 26;

        if (numeroPagina > 1 && eixoY == 50) {
            // Linhas de correção do quadrante
            canvas.drawLine(30, eixoY, 870, eixoY, paintLinhas);
        }

        if (numeroPagina > 1 && eixoY == 50) {
            // Linhas de correção do quadrante
            canvas.drawLine(30, eixoY, 870, eixoY, paintLinhas);
        }

        int centralizaInfo = 10;
        ArrayList<String> linhas = quebrarLinhas("DESCRIÇÃO DO PRODUTO", 60);
        for (String produtoDescricao : linhas) {
            canvas.drawText(produtoDescricao, 160, eixoYTexto, paintTexto);
            eixoYTexto += 20;
            centralizaInfo += 10;
        }
        if (linhas.toArray().length > 1) {
            for (int i = 0; i < (linhas.toArray().length - 1); i++) {
                incrementaY += 20;
            }
        }

        canvas.drawText("123456789", 40, eixoYTexto - centralizaInfo, paintTexto);
        canvas.drawText("10", 585 + (alinharQuantidade("10")), eixoYTexto - centralizaInfo, paintTexto);
        canvas.drawText("R$ 100,00", 730 - (alinharValor("R$ 100,00")), eixoYTexto - centralizaInfo, paintTexto);
        canvas.drawText("R$ 1.000,00", 860 - (alinharValor("R$ 1.000,00")), eixoYTexto - centralizaInfo, paintTexto);

        // Horizontal abaixo
        canvas.drawLine(30, incrementaY + 40, 870, incrementaY + 40, paintLinhas);
        // Vertical
        canvas.drawLine(30, eixoY, 30, incrementaY + 40, paintLinhas);
        canvas.drawLine(870, eixoY, 870, incrementaY + 40, paintLinhas);
        canvas.drawLine(150, eixoY, 150, incrementaY + 40, paintLinhas);
        canvas.drawLine(570, eixoY, 570, incrementaY + 40, paintLinhas);
        canvas.drawLine(620, eixoY, 620, incrementaY + 40, paintLinhas);
        canvas.drawLine(740, eixoY, 740, incrementaY + 40, paintLinhas);

        incrementaY += 40;
    }

    private static void desenharTotalPedido(Canvas canvas, String nomeCampo, int eixoX) {
        if (numeroPagina > 1 && incrementaY == 50) {
            // Desenha Total Pedido com linhas de correção do quadrante
            canvas.drawLine(30, incrementaY, 870, incrementaY, paintLinhas);
            canvas.drawLine(30, eixoY + 40, 870, incrementaY + 40, paintLinhas);
            canvas.drawLine(30, eixoY, 30, incrementaY + 40, paintLinhas);
            canvas.drawLine(870, eixoY, 870, incrementaY + 40, paintLinhas);
            canvas.drawLine(620, eixoY, 620, incrementaY + 40, paintLinhas);
            canvas.drawLine(740, eixoY, 740, incrementaY + 40, paintLinhas);
            canvas.drawText(nomeCampo, eixoX, eixoYTexto, paintTextoNegrito);
            canvas.drawText("R$ 10.000,00", 860 - (alinharValor("R$ 10.000,00")), eixoYTexto, paintTextoNegrito);
        } else {
            canvas.drawLine(30, incrementaY + 40, 870, incrementaY + 40, paintLinhas);
            canvas.drawLine(30, eixoY, 30, incrementaY + 40, paintLinhas);
            canvas.drawLine(870, eixoY, 870, incrementaY + 40, paintLinhas);
            canvas.drawLine(620, eixoY, 620, incrementaY + 40, paintLinhas);
            canvas.drawLine(740, eixoY, 740, incrementaY + 40, paintLinhas);
            canvas.drawText(nomeCampo, eixoX, eixoYTexto + 20, paintTextoNegrito);
            canvas.drawText("R$ 10.000,00", 860 - (alinharValor("R$ 10.000,00")), eixoYTexto + 20, paintTextoNegrito);
        }
    }

    private static void desenharRodape(Canvas canvas) {
        // Linhas Horizontais
        canvas.drawLine(30, eixoY, 870, eixoY, paintLinhas);
        canvas.drawLine(30, eixoY + 40, 870, eixoY + 40, paintLinhas);
        canvas.drawLine(30, eixoY + 80, 450, eixoY + 80, paintLinhas);
        canvas.drawLine(30, eixoY + 120, 450, eixoY + 120, paintLinhas);
        canvas.drawLine(30, eixoY + 160, 870, eixoY + 160, paintLinhas);

        // Linhas Verticais
        canvas.drawLine(30, eixoY, 30, eixoY + 160, paintLinhas);
        canvas.drawLine(870, eixoY, 870, eixoY + 160, paintLinhas);
        canvas.drawLine(450, eixoY, 450, eixoY + 160, paintLinhas);
        canvas.drawLine(220, eixoY + 40, 220, eixoY + 160, paintLinhas);

        // Texto
        canvas.drawText("OUTRAS INFORMAÇÕES DO PEDIDO", 245, eixoYTexto, paintTitulo);
        canvas.drawText("OBSERVAÇÕES PARA A FATURA", 655, eixoYTexto, paintTitulo);
        canvas.drawText("Forma de Pagamento", 40, eixoYTexto + 40, paintTexto);
        canvas.drawText("Condição de Pagamento", 40, eixoYTexto + 80, paintTexto);
        canvas.drawText("Representante / Vendedor", 40, eixoYTexto + 120, paintTexto);

        canvas.drawText("DINHEIRO", 230, eixoYTexto + 40, paintTextoNegrito);
        canvas.drawText("1x Parcela", 230, eixoYTexto + 80, paintTextoNegrito);
        canvas.drawText("Nome Vendedor", 230, eixoYTexto + 120, paintTextoNegrito);
        ArrayList<String> linhas = quebrarLinhas("Teste referente ao campo de observações para a fatura do cliente", 50);
        for (String observacaoNotaFiscal : linhas) {
            canvas.drawText(observacaoNotaFiscal, 460, incrementaY + 66, paintTexto);
            incrementaY += 20;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void desenharMensagemRodape(PdfDocument.Page pagina, Canvas canvas) {
        String mensagemRodape = "Mensagem customizável para o rodapé do PDF.";
        canvas.drawText(mensagemRodape, pagina.getInfo().getPageWidth() / 2, eixoYTexto + 180, paintTitulo);
    }

    private static int alinharQuantidade(String qtd) {
        int alinhamento = 0;
        if (qtd.length() >= 3) {
            return alinhamento;
        } else if (qtd.length() == 2) {
            alinhamento = 3;
            return alinhamento;
        } else {
            alinhamento = 7;
            return alinhamento;
        }
    }

    private static int alinharValor(String valor) {
        int alinhamento = 0;
        for (int i = 0; i < valor.length(); i++) {
            alinhamento += 6;
        }
        if (valor.length() >= 4) {
            alinhamento -= 2;
            if (valor.length() >= 5) {
                alinhamento += 1;
            }
        }
        return alinhamento;
    }

    private static void configurarPaintPdf() {
        paintLinhas = configurarPaintLinhas();
        paintTitulo = configurarPaintTitulo();
        paintTextoNegrito = configurarPaintTextoNegrito();
        paintTexto = configurarPaintTextoPadrao();
    }

    private static Paint configurarPaintTextoPadrao() {
        Paint paintTexto = new Paint();
        paintTexto.setTextSize(12);
        return paintTexto;
    }

    private static Paint configurarPaintTextoNegrito() {
        Paint paintTextoNegrito = new Paint();
        paintTextoNegrito.setTextSize(12);
        paintTextoNegrito.setFakeBoldText(true);
        return paintTextoNegrito;
    }

    private static Paint configurarPaintTitulo() {
        Paint paintTitulo = new Paint();
        paintTitulo.setTextAlign(Paint.Align.CENTER);
        paintTitulo.setTextSize(15);
        paintTitulo.setFakeBoldText(true);
        return paintTitulo;
    }

    private static Paint configurarPaintLinhas() {
        Paint paintLinhas = new Paint();
        paintLinhas.setStrokeWidth(1f);
        return paintLinhas;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static PdfDocument.Page gerarPrimeiraPagina(PdfDocument pdfDocument) {
        numeroPagina = 1;
        PdfDocument.PageInfo detalhesPagina = new PdfDocument.PageInfo.Builder(900, 1200, numeroPagina).create();
        return pdfDocument.startPage(detalhesPagina);
    }

    private static void iniciarOrientacaoPagina() {
        eixoY = 50;
        eixoYTexto = 76;
        incrementaY = 50;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static PdfDocument.Page iniciarNovaPagina(PdfDocument pdfDocument, int numeroPagina) {
        PdfDocument.PageInfo detalhesPagina;
        PdfDocument.Page pagina;
        detalhesPagina = new PdfDocument.PageInfo.Builder(900, 1200, numeroPagina).create();
        pagina = pdfDocument.startPage(detalhesPagina);
        return pagina;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static int finalizarPagina(PdfDocument pdfDocument, int numeroPagina, PdfDocument.Page pagina) {
        numeroPagina += 1;
        pdfDocument.finishPage(pagina);
        return numeroPagina;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void salvarPDF(Context context, PdfDocument pdfDocument) {
        String fileName = "Teste.pdf";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName);
        try
        {
            pdfDocument.writeTo(new FileOutputStream(file));

            if (file.exists() && (file.length() > 0L))
            {
                Uri pdfUri = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    pdfUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                } else {
                    pdfUri = Uri.fromFile(file);
                }

                abrirPDF(context, file, pdfUri);
            }

            ((Activity) context).finish();
        } catch (Exception ignored) {
        }
    }

    private static void abrirPDF(Context context, File file, Uri pdfUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, file.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

    private static ArrayList<String> quebrarLinhas(String texto, int delimitador) {
        ArrayList<String> linhas = new ArrayList<>();
        while (texto.length() > delimitador) {
            String linha = texto.substring(0, delimitador);
            texto = texto.replace(linha, "");
            if (linha.startsWith(" ")) {
                linha = linha.replaceFirst(" ", "");
            }
            linhas.add(linha);
        }
        linhas.add(texto);
        return linhas;
    }

    private static Bitmap redimensionarImagem(int arquivoImagem) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(String.valueOf(arquivoImagem), bitmapOptions);
        int larguraImagem = bitmapOptions.outWidth;
        int alturaImagem = bitmapOptions.outHeight;

        int fatorEscala = 1;
        if ((larguraImagem > 200) || (alturaImagem > 100)) {
            fatorEscala = Math.max(larguraImagem / 200, alturaImagem / 100);
        }

        bitmapOptions.inJustDecodeBounds = false;
        bitmapOptions.inSampleSize = fatorEscala;

        return BitmapFactory.decodeFile(String.valueOf(arquivoImagem), bitmapOptions);
    }
}
