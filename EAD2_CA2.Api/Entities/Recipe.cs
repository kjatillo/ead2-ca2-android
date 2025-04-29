using System.ComponentModel.DataAnnotations;

namespace EAD2_CA2.Api.Entities;

public class Recipe
{
    [Key]
    public int RecipeId { get; set; }
    [Required]
    public string Name { get; set; } = string.Empty;
    [Required]
    public string Category { get; set; } = string.Empty;
    [Required]
    public string Cuisine { get; set; } = string.Empty;
    [Required]
    public string Ingredients { get; set; } = string.Empty;
    [Required]
    public string Instructions { get; set; } = string.Empty;
    public string? ImageUrl { get; set; }
    public string? VideoUrl { get; set; }
    [Required]
    public double Calories { get; set; }
}
