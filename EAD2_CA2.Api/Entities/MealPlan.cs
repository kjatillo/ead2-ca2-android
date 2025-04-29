using EAD2_CA2.Api.Enums;
using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;

namespace EAD2_CA2.Api.Entities;

public class MealPlan
{
    [Key]
    public int MealPlanId { get; set; }

    [ForeignKey(nameof(RecipeId))]
    public int RecipeId { get; set; }
    [Required]
    public Day DayOfWeek { get; set; }
    [Required]
    public MealType MealType { get; set; }
    public string? Notes { get; set; } = string.Empty;
}
