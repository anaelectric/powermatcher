package net.powermatcher.integration.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.zip.DataFormatException;

import net.powermatcher.api.data.Price;
import net.powermatcher.integration.base.BidResilienceTest;
import net.powermatcher.mock.MockAgent;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validate the correct behaviour of the components when incorrect prices are inserted.
 * 
 * @author IBM
 *
 */
public class SendReceivePriceTestCPQ1 extends BidResilienceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendReceivePriceTestCPQ1.class);

    /**
     * The auctioneer is invoked to publish a null price. The auctioneer will not publish the null price but reset its
     * publish-state.
     * 
     * Check if the value of the last published price is not the null price but the most recent valid price.
     * 
     * @throws IOException
     * @throws DataFormatException
     */
    @Test
    public void publicationNullPriceAuctioneerCPQ1() throws IOException, DataFormatException {

        // Prepare the test for reading test input
        prepareTest("CPQ/CPQ1", null);

        // Send bids to the matcherAgent (concentrator)
        sendBidsToMatcher();
        timer.doTaskOnce();

        // Validate if concentrator receives correct price
        assertEquals(this.resultsReader.getEquilibriumPrice(), this.concentrator.getLastPrice().getCurrentPrice(), 0);

        // Send null price
        Price nullPrice = null;
        this.auctioneer.publishPrice(nullPrice);

        // Validate if concentrator has rejected the incorrect price and retained the last correct price.
        assertEquals(true, (this.auctioneer.getLastPublishedPrice() == null));

        // Check the last received price. The auctioneer should not have published the null
        // price and the last price at the concentrator should be the price that was sent earlier.
        assertEquals(this.resultsReader.getEquilibriumPrice(), this.concentrator.getLastPrice().getCurrentPrice(), 0);

    }

    /**
     * A set of agents send a bid to the auctioneer via the concentrator. The concentrator is sent directly a null
     * price. The concentrator should reject it.
     * 
     * @throws IOException
     * @throws DataFormatException
     */
    @Test
    // (expected=IllegalArgumentException.class)
    public void rejectReceivalNullPriceConcentratorCPQ1() throws IOException, DataFormatException {

        // Prepare the test for reading test input
        prepareTest("CPQ/CPQ1", null);

        // Send bids to the matcherAgent (concentrator)
        sendBidsToMatcher();
        timer.doTaskOnce();
        // Check if concentrator received correct price
        assertEquals(this.resultsReader.getEquilibriumPrice(), this.concentrator.getLastReceivedPrice()
                .getCurrentPrice(), 0);

        // Send incorrect price directly to the concentrator
        Price falsePrice = null;
        try {
            this.concentrator.updatePrice(falsePrice);
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

        // Check if concentrator retains last received correct price
        assertEquals(this.resultsReader.getEquilibriumPrice(), this.concentrator.getLastPublishedPrice()
                .getCurrentPrice(), 0);
    }

    /**
     * A set of agents send a bid to the auctioneer via the concentrator. After sending a calculated (valid) price the
     * auctioneer will be forced to send an price that is outside its local price range. According to the specifications
     * this is permitted. Connected agents can have a different local price market base.
     * 
     * Check if the auctioneer publishes the price.
     * 
     * @throws IOException
     * @throws DataFormatException
     */
    @Test
    public void sendPriceOutsideMarketBaseAuctioneerCPQ1() throws IOException, DataFormatException {

        // Prepare the test for reading test input
        prepareTest("CPQ/CPQ1", null);

        // Send bids to the matcherAgent (concentrator)
        sendBidsToMatcher();
        timer.doTaskOnce();
        // Validate if concentrator receives correct price
        assertEquals(this.resultsReader.getEquilibriumPrice(), this.concentrator.getLastPrice().getCurrentPrice(), 0);

        // Send price outside range price
        Price price = new Price(this.marketBasis, 52.0d);
        this.auctioneer.publishPrice(price);

        // Validate if concentrator has received the new price
        assertEquals(price.getCurrentPrice(), this.auctioneer.getLastPublishedPrice().getCurrentPrice(), 0);

    }

    /**
     * A set of agents send a bid to the auctioneer via the concentrator. After sending a calculated (valid) price the
     * auctioneer will be forced to send an price that is outside its local price range. According to the specifications
     * this is permitted. Connected agents can have a different local price market base.
     * 
     * Check if the concentrator accepts the price.
     * 
     * @throws IOException
     * @throws DataFormatException
     */
    @Test
    public void acceptPriceOutsideRangeConcentratorCPQ1() throws IOException, DataFormatException {

        // Prepare the test for reading test input
        prepareTest("CPQ/CPQ1", null);

        // Send bids to the matcherAgent (concentrator)
        sendBidsToMatcher();
        timer.doTaskOnce();
        // Validate if concentrator receives correct price
        assertEquals(this.resultsReader.getEquilibriumPrice(), this.concentrator.getLastPrice().getCurrentPrice(), 0);

        // Send price outside range price
        Price price = new Price(this.marketBasis, 52.0d);
        this.auctioneer.publishPrice(price);

        // Validate if concentrator has received the new price
        assertEquals(price.getCurrentPrice(), this.concentrator.getLastPrice().getCurrentPrice(), 0);
    }

    /**
     * A set of agents send a bid to the auctioneer via the concentrator. After sending a calculated (valid) price the
     * auctioneer will be forced to send an price that is outside its local price range. According to the specifications
     * this is permitted. Connected agents can have a different local price market base. Check if the concentrator
     * publishes this price.
     * 
     * 
     * 
     * @throws IOException
     * @throws DataFormatException
     */
    @Test
    public void publishPriceOutsideRangeConcentratorCPQ1() throws IOException, DataFormatException {

        // Prepare the test for reading test input
        prepareTest("CPQ/CPQ1", null);

        // Send bids to the matcherAgent (concentrator)
        sendBidsToMatcher();
        timer.doTaskOnce();
        // Validate if concentrator receives correct price
        assertEquals(this.resultsReader.getEquilibriumPrice(), this.concentrator.getLastPrice().getCurrentPrice(), 0);

        // Send price outside range
        Price price = new Price(this.marketBasis, 52.0d);
        this.auctioneer.publishPrice(price);

        // Validate if concentrator publishes the price to the agents
        assertEquals(price.getCurrentPrice(), this.concentrator.getLastPublishedPrice().getCurrentPrice(), 0);
    }

    /**
     * A set of agents send a bid to the auctioneer via the concentrator. After sending a calculated (valid) price the
     * auctioneer will be forced to send an price that is outside its local price range. According to the specifications
     * this is permitted. Connected agents can have a different local price market base.
     * 
     * Check if the concentrator accepts the price and forwards the price to the agents.
     * 
     * @throws IOException
     * @throws DataFormatException
     */
    @Test
    public void retainmentLastValidPriceConcentratorCPQ1() throws IOException, DataFormatException {

        // Prepare the test for reading test input
        prepareTest("CPQ/CPQ1", null);

        // Send bids to the matcherAgent (concentrator)
        sendBidsToMatcher();
        timer.doTaskOnce();
        // Send incorrect price directly to the concentrator
        LOGGER.info("4. Sending incorrect price (null) by auctioneer");
        Price falsePrice = null;
        try {
            this.concentrator.updatePrice(falsePrice);
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

        // Check if concentrator retains last received correct price
        assertEquals(this.resultsReader.getEquilibriumPrice(), this.concentrator.getLastPrice().getCurrentPrice(), 0);
    }

    /**
     * A set of agents send a bid to the auctioneer via the concentrator. After sending a calculated (valid) price the
     * auctioneer will be forced to send an price that is outside its local price range. According to the specifications
     * this is permitted. Connected agents can have a different local price market base.
     * 
     * Check if the agent accepts the price.
     * 
     * @throws IOException
     * @throws DataFormatException
     */
    @Test
    public void acceptPriceOutsideRangeByAgentsCPQ1() throws IOException, DataFormatException {

        // Prepare the test for reading test input
        prepareTest("CPQ/CPQ1", null);

        // Send bids to the matcherAgent (concentrator)
        sendBidsToMatcher();
        timer.doTaskOnce();

        // Send price outside range
        Price price = new Price(this.marketBasis, 52.0d);
        this.auctioneer.publishPrice(price);
        
        // Verify the price received by the agents
        for (MockAgent agent : agentList) {
            assertEquals(price.getCurrentPrice(), agent.getLastPriceUpdate().getCurrentPrice(), 0);
        }
    }
}