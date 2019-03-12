package pt.ulisboa.tecnico.softeng.broker.domain

import spock.lang.*

import mockit.Expectations
import mockit.Mocked
import mockit.integration.junit4.JMockit
import pt.ulisboa.tecnico.softeng.broker.interfaces.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.interfaces.BankInterface
import pt.ulisboa.tecnico.softeng.broker.interfaces.HotelInterface
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type

public class AdventureProcessMethodMockTest {
	def ACTIVITY_REFERENCE = "activityReference"
	def HOTEL_REFERENCE = "hotelReference"
	def PAYMENT_CONFIRMATION = "paymentConfirmation"
	def IBAN = "BK01987654321"
	def begin = new LocalDate(2016, 12, 19)
	def end = new LocalDate(2016, 12, 21)
	@Shared def broker

	@Before
	def setUp() {
		this.broker = new Broker ("BR98", "Travel Light")
	}

	def processWithNoExceptions(@Mocked final BankInterface bankInterface,
			@Mocked final HotelInterface hotelInterface, @Mocked final ActivityInterface activityInterface) {
		new Expectations() {
			{
				given:
					BankInterface.processPayment(IBAN, 300)
				when:
					this.result = PAYMENT_CONFIRMATION
				then:
					thrown(BankException)

				given:
					HotelInterface.reserveHotel(Type.SINGLE, AdventureProcessMethodMockTest.this.begin, AdventureProcessMethodMockTest.this.end)
				when:
				this.result = HOTEL_REFERENCE
				then:
					thrown(HotelException)
				
				given:
					ActivityInterface.reserveActivity(AdventureProcessMethodMockTest.this.begin, AdventureProcessMethodMockTest.this.end, 20)				
				when:
					this.result = ACTIVITY_REFERENCE
				then:
					thrown(ActivityException)

			}
		}

		def adventure = new Adventure(this.broker, this.begin, this.end, 20, IBAN, 300)

		adventure.process()

		PAYMENT_CONFIRMATION == adventure.getBankPayment()
		HOTEL_REFERENCE == adventure.getRoomBooking()
		ACTIVITY_REFERENCE == adventure.getActivityBooking()
	}

	
	def tearDown() {
		Broker.brokers.clear()
	}

}
