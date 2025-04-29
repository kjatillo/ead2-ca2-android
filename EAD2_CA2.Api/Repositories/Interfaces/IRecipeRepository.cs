using EAD2_CA2.Api.Entities;

namespace EAD2_CA2.Api.Repositories.Interfaces;

public interface IRecipeRepository
{
    Task<IEnumerable<Recipe>> GetAllRecipesAsync();
    Task<Recipe?> GetRecipeByIdAsync(int recipeId);
    Task<IEnumerable<Recipe>> SearchRecipesByNameOrIngredientAsync(string query);
    Task<IEnumerable<Recipe>> FilterRecipesAsync(
        string? category,
        string? cuisine,
        int? minCalories,
        int? maxCalories,
        string? ingredient,
        bool? withImage,
        bool? withVideo);
}
