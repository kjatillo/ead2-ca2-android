using EAD2_CA2.Api.Entities;
using Microsoft.EntityFrameworkCore;

namespace EAD2_CA2.Api.Data;

public class MealPlannerContext : DbContext
{
    public MealPlannerContext(DbContextOptions<MealPlannerContext> options)
    : base(options) { }

    public required DbSet<Recipe> Recipes { get; set; }
    public required DbSet<MealPlan> MealPlans { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<MealPlan>()
            .Property(mp => mp.DayOfWeek)
            .HasConversion<string>();

        modelBuilder.Entity<MealPlan>()
            .Property(mp => mp.MealType)
            .HasConversion<string>();

        modelBuilder.Entity<Recipe>()
            .Property(r => r.RecipeId)
            .UseIdentityColumn(0, 1);

        modelBuilder.Entity<MealPlan>()
            .Property(mp => mp.MealPlanId)
            .UseIdentityColumn(1000, 1);
    }
}
