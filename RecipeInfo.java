import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecipeInfo {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o nome da receita que deseja buscar:");
        String recipeName = scanner.nextLine();

        try {
            // Formatar a URL da API do TheMealDB
            String url = "https://www.themealdb.com/api/json/v1/1/search.php?s=" + recipeName.replace(" ", "%20");

            // Criar um cliente HTTP
            HttpClient client = HttpClient.newHttpClient();

            // Criar uma solicitação HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            // Enviar a solicitação e obter a resposta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Verificar se a resposta foi bem-sucedida
            if (response.statusCode() == 200) {
                // Parsear o corpo da resposta JSON
                JSONObject jsonResponse = new JSONObject(response.body());
                JSONArray meals = jsonResponse.getJSONArray("meals");

                if (meals.length() > 0) {
                    System.out.println("Receitas Disponíveis:");
                    for (int i = 0; i < Math.min(meals.length(), 10); i++) {
                        JSONObject recipe = meals.getJSONObject(i);
                        System.out.println((i + 1) + ". " + recipe.optString("strMeal"));
                    }

                    System.out.println("Digite o número da receita que deseja visualizar:");
                    int choice = scanner.nextInt();

                    if (choice >= 1 && choice <= Math.min(meals.length(), 10)) {
                        // Obter as informações da receita escolhida
                        JSONObject chosenRecipe = meals.getJSONObject(choice - 1);
                        displayRecipeDetails(chosenRecipe);
                    } else {
                        System.out.println("Escolha inválida.");
                    }
                } else {
                    System.out.println("Nenhuma receita encontrada com esse nome.");
                }
            } else {
                System.out.println("Erro na consulta à API: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ocorreu um erro ao tentar buscar informações sobre a receita.");
        }

        scanner.close();
    }

    private static void displayRecipeDetails(JSONObject recipe) {
        // Exibir informações sobre a receita
        System.out.println("Nome: " + recipe.optString("strMeal", "Nome não disponível"));
        System.out.println("Categoria: " + recipe.optString("strCategory", "Categoria não disponível"));
        System.out.println("Área de Origem: " + recipe.optString("strArea", "Área de Origem não disponível"));
        System.out.println("Instruções:\n" + recipe.optString("strInstructions", "Instruções não disponíveis"));

        // Exibir ingredientes e medidas
        System.out.println("Ingredientes:");
        for (int i = 1; i <= 20; i++) {
            String ingredient = recipe.optString("strIngredient" + i);
            String measure = recipe.optString("strMeasure" + i);
            if (!ingredient.isEmpty() && !measure.isEmpty()) {
                System.out.println("- " + ingredient + ": " + measure);
            }
        }
    }
}
