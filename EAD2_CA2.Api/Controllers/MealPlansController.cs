using EAD2_CA2.Api.Entities;
using EAD2_CA2.Api.Repositories.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace EAD2_CA2.Api.Controllers;

[Route("api/[controller]")]
[ApiController]
public class MealPlansController : ControllerBase
{
    private readonly IMealPlanRepository _mealPlanRepository;

    public MealPlansController(IMealPlanRepository mealPlanRepository)
    {
        _mealPlanRepository = mealPlanRepository ?? throw new ArgumentNullException(nameof(mealPlanRepository));
    }

    [HttpPost]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public async Task<ActionResult<MealPlan>> AddMealPlan([FromBody] MealPlan mealPlan)
    {
        if (!ModelState.IsValid)
        {
            return BadRequest(ModelState);
        }

        var createdMealPlan = await _mealPlanRepository.AddMealPlanAsync(mealPlan);

        return CreatedAtAction(
            nameof(GetMealPlanById),
            new { mealPlanId = createdMealPlan.MealPlanId },
            createdMealPlan);
    }

    [HttpGet("{mealPlanId}")]
    [ProducesResponseType(typeof(MealPlan), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<MealPlan>> GetMealPlanById(int mealPlanId)
    {
        var mealPlan = await _mealPlanRepository.GetMealPlanByIdAsync(mealPlanId);
        if (mealPlan == null)
        {
            return NotFound();
        }

        return Ok(mealPlan);
    }

    [HttpGet]
    [ProducesResponseType(typeof(IEnumerable<MealPlan>), StatusCodes.Status200OK)]
    public async Task<ActionResult> GetMealPlans([FromQuery] Enums.Day? day)
    {
        if (day.HasValue)
        {
            var mealPlans = await _mealPlanRepository.GetMealPlanByDayAsync(day.Value);

            return Ok(mealPlans);
        }
        else
        {
            var mealPlans = await _mealPlanRepository.GetAllMealPlansAsync();

            return Ok(mealPlans);
        }
    }

    [HttpPut("{mealPlanId}")]
    [ProducesResponseType(typeof(IEnumerable<MealPlan>), StatusCodes.Status200OK)]
    public async Task<IActionResult> EditMealPlan(int mealPlanId, MealPlan updateMealPlan)
    {
        var mealPlan = await _mealPlanRepository.GetMealPlanByIdAsync(mealPlanId);
        if (mealPlan == null) return NotFound();

        mealPlan.RecipeId = updateMealPlan.RecipeId;
        mealPlan.DayOfWeek = updateMealPlan.DayOfWeek;
        mealPlan.MealType = updateMealPlan.MealType;
        mealPlan.Notes = updateMealPlan.Notes;

        var updatedMealPlanResult = await _mealPlanRepository.EditMealPlanAsync(mealPlan);

        return Ok(updatedMealPlanResult);
    }

    [HttpDelete("{mealPlanId}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> DeleteMealPlan(int mealPlanId)
    {
        var mealPlan = await _mealPlanRepository.GetMealPlanByIdAsync(mealPlanId);
        if (mealPlan == null) return NotFound();

        await _mealPlanRepository.DeleteMealPlanAsync(mealPlanId);

        return NoContent();
    }
}