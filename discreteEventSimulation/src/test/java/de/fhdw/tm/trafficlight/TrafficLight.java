package de.fhdw.tm.trafficlight;

import java.util.ArrayList;
import java.util.List;

import de.fhdw.tm.des.evaluation.EvaluationInterval;
import de.fhdw.tm.des.evaluation.SimulationEvaluatorWithStore;
import de.fhdw.tm.des.evaluation.aggregation.CountCharacteristic;
import de.fhdw.tm.des.evaluation.aggregation.MeanCharacteristic;
import de.fhdw.tm.des.modelling.ModelProcess;
import de.fhdw.tm.des.modelling.ProcessStep;
import de.fhdw.tm.des.modelling.ProcessStepDelay;
import de.fhdw.tm.des.scheduler.DESScheduler;

public class TrafficLight {

	private List<Car> waitingCars;
	private Integer carLeavingTime;
	long timeleft;
	private Integer id;
	private Crossing crossing;
	private SimulationEvaluatorWithStore waitingCarsStats;
	private SimulationEvaluatorWithStore queueSize;

	public TrafficLight(Crossing crossing, long timeleft, Integer carLeavingTime, Integer id) {
		this.id = id;
		this.crossing = crossing;
		this.waitingCars = new ArrayList<Car>();
		this.timeleft = timeleft;
		this.carLeavingTime = carLeavingTime;
		this.waitingCarsStats = new SimulationEvaluatorWithStore("waitingCars" + this.id, this, new CountCharacteristic(), new MeanCharacteristic()) {
		};
		this.queueSize = new SimulationEvaluatorWithStore("queueSize", new Object(), new CountCharacteristic(), new MeanCharacteristic()) {};
	}

	public void triggerQueueStats() {
		this.queueSize.addData(waitingCars.size());
	}

	public void carArrives(Car car) {
		this.waitingCars.add(car);
	}

	@ProcessStepDelay(0)
	public long carLeavesDelay() {
		return 0;
	}

	@ProcessStep(0)
	public void carLeaves() {
		if (this.timeleft >= this.carLeavingTime) {
			this.timeleft -= this.carLeavingTime;
			if (!this.waitingCars.isEmpty()) 
				this.waitingCarsStats.addData(DESScheduler.getSimulationTime()-this.waitingCars.remove(0).getArivalTime());
			DESScheduler.scheduleToFuture(new ModelProcess(this), this.carLeavingTime);
		} else
			this.crossing.nextLight();
	}

	@Override
	public String toString() {
		return "Light = " + this.id + ", waiting cars = " + this.waitingCars + ", time left = " + this.timeleft;
	}

}
