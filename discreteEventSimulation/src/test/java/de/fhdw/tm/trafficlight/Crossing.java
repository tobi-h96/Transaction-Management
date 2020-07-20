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
	private Integer currentLightId;
	private TrafficLight currentTrafficLight;
	private Integer greenPhaseTime;
	private Integer redPhaseTime;
	private Integer numberOfLights;
	private Boolean slowStart;
	private ExponentialDistribution slowStartDistribution;

	public Crossing(Integer numberOfLights, Integer greenPhaseTime, Integer redPhaseTime, Integer vehicleLeavingTime,
			Integer vehicleArrivingMean, Integer slowStartMean, boolean slowStart) {
		this.slowStartDistribution = new ExponentialDistribution(DESScheduler.getRandom(), slowStartMean);
		this.slowStart = slowStart;
		this.greenPhaseTime = greenPhaseTime;
		this.redPhaseTime = redPhaseTime;
		this.currentLightId = 0;
		this.numberOfLights = numberOfLights;
		this.trafficLights = new HashMap<Integer, TrafficLight>();
		for (int i = currentLightId; i < numberOfLights; i++) {
			TrafficLight newLight = new TrafficLight(i);
			this.trafficLights.put(i, newLight);
			DESScheduler.scheduleToFuture(
					new ModelProcess(new VehicleArrival(newLight, vehicleArrivingMean, vehicleLeavingTime)), 0);
		}
		this.currentTrafficLight = this.trafficLights.get(this.currentLightId);
	}

	@ProcessStepDelay(0)
	public long setUpDelay() {
		return 0; // doesnt get called -> see implementation modelling
	}

	@ProcessStep(0)
	public void setUp() {
		this.currentTrafficLight = this.trafficLights
				.get((this.currentLightId = ++this.currentLightId % numberOfLights));
	}

	@ProcessStepDelay(1)
	public long redPhase() {
		return this.redPhaseTime;
	}

	@ProcessStep(1)
	public void greenPhase() throws Exception {

		if (slowStart)
			this.currentTrafficLight.prepareGreenPhase(DESScheduler.getSimulationTime() + this.greenPhaseTime,
					(int) this.slowStartDistribution.sample());
		else
			this.currentTrafficLight.prepareGreenPhase(DESScheduler.getSimulationTime() + this.greenPhaseTime);

		DESScheduler.scheduleToFuture(new ModelProcess(this.currentTrafficLight), 0);
		DESScheduler.scheduleToFuture(new ModelProcess(this), this.greenPhaseTime);
	}
}
