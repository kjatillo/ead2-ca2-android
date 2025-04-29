using EAD2_CA2.Api.Entities;
using EAD2_CA2.Api.Repositories.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace EAD2_CA2.Api.Controllers;

[Route("api/[controller]")]
[ApiController]
public class RecipesController : Controller
{
    private readonly IRecipeRepository _recipeRepository;

    public RecipesController(IRecipeRepository recipeRepository)
    {
        _recipeRepository = recipeRepository ?? throw new ArgumentNullException(nameof(recipeRepository));
    }

    [HttpGet]
    [ProducesResponseType(typeof(IEnumerable<Recipe>), StatusCodes.Status200OK)]
    public async Task<IActionResult> GetAllRecipes()
    {
        var recipes = await _recipeRepository.GetAllRecipesAsync();

        return Ok(recipes);
    }

    [HttpGet("{recipeId}")]
    [ProducesResponseType(typeof(Recipe), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> GetRecipeById(int recipeId)
    {
        var recipe = await _recipeRepository.GetRecipeByIdAsync(recipeId);
        if (recipe == null) return NotFound();

        return Ok(recipe);
    }

    [HttpGet("search")]
    [ProducesResponseType(typeof(IEnumerable<Recipe>), StatusCodes.Status200OK)]
    public async Task<IActionResult> SearchRecipesByNameOrIngredient([FromQuery] string query)
    {
        var recipes = await _recipeRepository.SearchRecipesByNameOrIngredientAsync(query);

        return Ok(recipes);
    }

    [HttpGet("filter")]
    [ProducesResponseType(typeof(IEnumerable<Recipe>), StatusCodes.Status200OK)]
    public async Task<IActionResult> FilterRecipes(
    [FromQuery] string? category,
    [FromQuery] string? cuisine,
    [FromQuery] int? minCalories,
    [FromQuery] int? maxCalories,
    [FromQuery] string? ingredient,
    [FromQuery] bool? withImage,
    [FromQuery] bool? withVideo)
    {
        var recipes = await _recipeRepository
            .FilterRecipesAsync(category, cuisine, minCalories, maxCalories, ingredient, withImage, withVideo);

        return Ok(recipes);
    }
}
