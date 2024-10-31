package exercicios.consulta_receitas;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class principal {
    public static void main(String[] args) throws IOException, InterruptedException {

        Scanner leitura = new Scanner(System.in);
        System.out.println("======================================================================");
        System.out.println("Informe o nome de uma receita para obter informações: ");
        System.out.println("======================================================================");
        String nomeReceita = leitura.nextLine().toLowerCase();

        String endereco = "https://www.themealdb.com/api/json/v1/1/search.php?s=" + nomeReceita;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(endereco)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject receitas = new JSONObject(response.body());
        JSONArray meals = receitas.optJSONArray("meals");

        if (meals != null && meals.length() > 0) {
            JSONObject receita = meals.getJSONObject(0);

            StringBuilder textoTraduzido = new StringBuilder();
            textoTraduzido.append("NOME: ").append(receita.getString("strMeal")).append(System.lineSeparator());
            textoTraduzido.append("CATEGORIA: ").append(receita.getString("strCategory")).append(System.lineSeparator());
            textoTraduzido.append("PAÍS: ").append(receita.getString("strArea")).append(System.lineSeparator());
            textoTraduzido.append("INSTRUÇÕES: ").append(receita.getString("strInstructions")).append(System.lineSeparator());
            textoTraduzido.append("INGREDIENTES & MEDIDAS: ").append(System.lineSeparator());


            for (int i = 1; i <= 20; i++) {
                String ingrediente = receita.optString("strIngredient" + i, null);
                String medidas = receita.optString("strMeasure" + i, null);

                if (ingrediente != null && !ingrediente.isEmpty()) {
                    textoTraduzido.append("- ").append(ingrediente).append(" (").append(medidas).append(")\n");
                }
            }

            // Traduzindo o texto com as informações
            String traducaoPortugues = traduzirParaPortugues(textoTraduzido.toString());

            System.out.println(traducaoPortugues);

        } else {
            System.out.println("Receita não encontrada!");
        }
    }

    public static String traduzirParaPortugues(String textoIngles) throws IOException, InterruptedException {
        String chave = "AIzaSyBAVK4RtU1RUD3UXA59ssLshhnYn2YGStM";
        String urlTraducao = "https://translation.googleapis.com/language/translate/v2?key=" + chave;


        String postBody = "{\"q\":\"" + textoIngles + "\", \"target\":\"pt\"}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestTraducao = HttpRequest.newBuilder()
                .uri(URI.create(urlTraducao))
                .POST(HttpRequest.BodyPublishers.ofString(postBody))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> responseTraducao = client.send(
                requestTraducao, HttpResponse.BodyHandlers.ofString());

        JSONObject jsonResponse = new JSONObject(responseTraducao.body());

        return jsonResponse.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("translatedText");
    }
}


