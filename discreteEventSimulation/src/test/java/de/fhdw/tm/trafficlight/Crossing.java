package de.fhdw.tm.trafficlight;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.distribution.ExponentialDistribution;

import de.fhdw.tm.des.modelling.ModelProcess;
import de.fhdw.tm.des.modelling.ProcessStep;
import de.fhdw.tm.des.modelling.ProcessStepDelay;
import de.fhdw.tm.des.scheduler.DESScheduler;

public class Crossing {

	private Map<Integer, TrafficLight> trafficLights;
	private Integer currentLight;
	private Integer greenPhaseTime;
	private Integer redPhaseTime;
	private Integer carLeavingTime;
	private Integer numberOfLights;
	private Boolean slowStart;
	private ExponentialDistribution slowStartDistribution;

	public Crossing(Integer numberOfLights, Integer greenPhaseTime, Integer redPhaseTime, Integer carLeavingTime,
			Integer slowStartMean, boolean slowStart) {
		this.slowStartDistribution = new ExponentialDistribution(DESScheduler.getRandom(), slowStartMean);
		this.slowStart = slowStart;
		this.greenPhaseTime = greenPhaseTime;
		this.redPhaseTime = redPhaseTime;
		this.carLeavingTime = carLeavingTime;
		this.currentLight = 0;
		this.numberOfLights = numberOfLights;
		this.trafficLights = new HashMap<Integer, TrafficLight>();
		for (int i = currentLight; i < numberOfLights; i++) {
			TrafficLight newLight = new TrafficLight(this, this.greenPhaseTime, this.carLeavingTime, i);
			this.trafficLights.put(i, newLight);
			DESScheduler.scheduleToFuture(new ModelProcess(new CarArrival(newLight, greenPhaseTime / carLeavingTime)),
					0);
		}
	}

	@ProcessStepDelay(0)
	public long redPhase() {
		return this.redPhaseTime;
	}

	@ProcessStep(0)
	public void greenPhase() {
		TrafficLight currentTrafficLight = this.trafficLights.get(this.currentLight);
		if (slowStart)
			currentTrafficLight.prepareGreenPhase(this.greenPhaseTime, (int) this.slowStartDistribution.sample());
		else
			currentTrafficLight.prepareGreenPhase(this.greenPhaseTime);
		DESScheduler.log("Start Green Phase: " + currentTrafficLight.toString());
		DESScheduler.scheduleToFuture(new ModelProcess(currentTrafficLight), 0);
	}

	public void nextLight() {
		TrafficLight currentTrafficLight = this.trafficLights.get(this.currentLight);
		DESScheduler.log("End Green Phase: " + currentTrafficLight.toString());
		this.currentLight = ++this.currentLight % numberOfLights;
		DESScheduler.scheduleToFuture(new ModelProcess(this), 0);
	}
}
