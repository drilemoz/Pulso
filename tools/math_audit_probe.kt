import br.com.pulsobateria.core.*
import kotlin.math.abs
fun eq(a: Double,b: Double) { check(abs(a-b)<0.0001) { "$a != $b" } }
fun main() {
 val a=ObservedSessionPart(Mode.DISCHARGING,0,3_600_000,80,70,3_600_000,15)
 val b=ObservedSessionPart(Mode.DISCHARGING,3_780_000,5_580_000,70,65,1_800_000,15)
 val g=SessionArithmetic.group(listOf(a,b))!!
 check(g.observedDurationMs==5_400_000L && g.unobservedBetweenMs==180_000L)
 check(g.initialPercent-g.finalPercent==15);eq(g.ratePercentHour!!,-10.0)
 check(SessionArithmetic.group(listOf(a,b.copy(initialPercent=66)))==null)
 check(SessionArithmetic.group(listOf(a,b.copy(mode=Mode.CHARGING)))==null)
 val c=ObservedSessionPart(Mode.CHARGING,0,7_200_000L,30,50,7_200_000L,12)
 val d=ObservedSessionPart(Mode.CHARGING,7_200_000L,10_800_000L,50,60,3_600_000L,12)
 val charge=SessionArithmetic.group(listOf(c,d))!!
 eq(charge.ratePercentHour!!,10.0)
 check(charge.observedDurationMs==10_800_000L)
 check(BatteryMath.startsNewSession(Mode.CHARGING,Mode.DISCHARGING,10_000L,70_000L,100_000L,160_000L))
 check(BatteryMath.observedDuration(0,0,70_000L,false,0)==0L)
 val now=1_780_000_000_000L
 val histDis=DischargeForecast.history(listOf(
  HistoricalDischarge(now-2*3_600_000L,now-3_600_000L,3_600_000L,80,55,15,"DISCHARGING"),
  HistoricalDischarge(now-6*3_600_000L,now-3*3_600_000L,3*3_600_000L,80,50,15,"DISCHARGING")
 ),now)!!
 eq(histDis.percentPerHour,55.0/4)
 val histCharge=ChargeForecast.history(listOf(
  HistoricalCharge(now-3_660_000L,now-60_000L,3_600_000L,20,40,15,"CHARGING","USB"),
  HistoricalCharge(now-16*24*3_600_000L,now-15*24*3_600_000L,3_600_000L,20,55,15,"CHARGING","USB"),
  HistoricalCharge(now-6*24*3_600_000L,now-5*24*3_600_000L,3_600_000L,20,35,15,"CHARGING","Tomada")
 ),now,"USB")!!
 eq(histCharge.matchingSourcePercentPerHour!!,20.0)
 check(histCharge.matchingSourceSessionsUsed==1)
 check(BatteryMath.currentMa(1_234_000,Mode.CHARGING)==1234.0)
 check(BatteryMath.temperature(378)==37.8)
 check(BatteryMath.remainingMs(70,30.0,Mode.CHARGING)==3_600_000L)
 println("PASS: 15 conjuntos de verificações Kotlin/JVM de cálculos reais")
}
