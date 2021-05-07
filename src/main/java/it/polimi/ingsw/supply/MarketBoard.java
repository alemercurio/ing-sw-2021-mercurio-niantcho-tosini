package it.polimi.ingsw.supply;

import it.polimi.ingsw.cards.Color;
import it.polimi.ingsw.cards.DevelopmentCard;

import java.util.*;

/**
 * The MarketBoard class represents both the Market Tray and the Development Cards matrix of decks.
 * It offers methods for gathering Resources as Market Marbles and to buy a Development Card.
 */
public class MarketBoard {
    public final ResourceMarket resourceMarket;
    public final CardMarket cardMarket;

    /**
     * Represents the Market Tray with the Market Marbles, represented by the corresponding Resource.
     */
    public static class ResourceMarket {
        private final Resource[][] marketTray;
        private Resource remaining;

        /**
         * Constructs a ResourceMarket containing the given Resources in random order.
         * if the Resources provided are not enough to fill the Market Tray,
         * empty spaces are filled with VOID (white Marble);
         * if the pack has more resources than necessary the excess is ignored.
         * @param resources the ResourcePack to initialize the market with.
         */
        public ResourceMarket(ResourcePack resources) {
            this.marketTray = new Resource[3][4];
            for(int r = 0;r < 3;r++)
                for(int c = 0;c < 4;c++)
                    this.marketTray[r][c] = resources.getRandom();
            this.remaining = resources.getRandom();
        }

        /**
         * Constructs a standard ResourceMarket.
         */
        public ResourceMarket() {
            // Standard Resources for the marketTray:
            //      2 coins,
            //      2 stones,
            //      2 servants,
            //      2 shields,
            //      1 faithpoints,
            //      4 voids

            this(new ResourcePack(2,2,2,2,1,4));
        }

        /**
         * Gathers Resources from the given column; marbles shift is automatically handled.
         * @param column the column whose resources are desired.
         * @return a pack of the Resources previously present in the given column.
         */
        public ResourcePack getColumn(int column) {
            ResourcePack resources = new ResourcePack();

            //adjust the index of column
            if(column >= 4) column = 3;
            else if(column < 0) column = 0;

            //gathers resources and shifts them
            Resource tmp_resource;
            for(int row = 2;row >= 0; row--) {
                tmp_resource = this.marketTray[row][column];
                resources.add(tmp_resource,1);
                this.marketTray[row][column] = this.remaining;
                this.remaining = tmp_resource;
            }

            return resources;
        }

        /**
         * Gathers Resources from the given row; marbles shift is automatically handled.
         * @param row the row whose resources are desired.
         * @return a pack of the resources previously present in the given row.
         */
        public ResourcePack getRow(int row) {
            ResourcePack resources = new ResourcePack();

            // Adjust the index of column
            if(row >= 3) row = 2;
            else if(row < 0) row = 0;

            // Gathers resources and shifts them
            Resource tmp_resource;
            for(int column = 3;column >= 0; column--) {
                tmp_resource = this.marketTray[row][column];
                resources.add(tmp_resource,1);
                this.marketTray[row][column] = this.remaining;
                this.remaining = tmp_resource;
            }
            return resources;
        }

        @Override
        public String toString() {
            StringBuilder resMarket = new StringBuilder("{");
            for(int r = 0;r < 3;r++) {
                resMarket.append("\n\t");
                for (int c = 0; c < 4; c++) {
                    resMarket.append(marketTray[r][c].getAlias());
                }
            }
            resMarket.append("\n}");
            return resMarket.toString();
        }
    }

    /**
     * Represents the Development Cards section of the MarketBoard.
     */
    public static class CardMarket {
        private final Map<Color, List<LinkedList<DevelopmentCard>>> decksMap;

        /**
         * Constructs a CardMarket and loads DevelopmentCards into it.
         */
        public CardMarket() {
            // initialize data structure
            this.decksMap = new HashMap<>();
            for(Color color : Color.values()) {
                List<LinkedList<DevelopmentCard>> column = new ArrayList<>();
                for(int i = 0; i < 3; i++) {
                    column.add(new LinkedList<>());
                }
                this.decksMap.put(color,column);
            }
            // load Development Cards
            List<DevelopmentCard> devCards = DevelopmentCard.getDevelopmentCardDeck("src/main/resources/JSON/DevelopmentCard.json");
            Collections.shuffle(devCards);
            for(DevelopmentCard card : devCards)
                this.decksMap.get(card.getColor()).get(card.getLevel() - 1).add(card);
        }

        /**
         * Returns the cost of the first DevelopmentCard available with
         * the specified Color and level; if no Card matches
         * the given parameters throws NoSuchDevelopmentCardException.
         * @param level the level of the required Card.
         * @param color the color of the required Card.
         * @return the ResourcePack representing the cost of the required Card.
         * @throws NoSuchDevelopmentCardException if no Card matches the given parameters.
         */
        public ResourcePack getCost(int level, Color color) throws NoSuchDevelopmentCardException {
            LinkedList<DevelopmentCard> stack = this.decksMap.get(color).get(level - 1);

            if(stack.isEmpty()) throw new NoSuchDevelopmentCardException();
            else return stack.getFirst().getCost();
        }

        /**
         * Retrieves, but does not remove, the DevelopmentCard on top of the specified deck;
         * the given color specifies the column as level does for the row;
         * if the deck is empty throws NoSuchDevelopmentCardException.
         * @param level the level of the required DevelopmentCard.
         * @param color the color of the required DevelopmentCard.
         * @return the required DevelopmentCard.
         * @throws NoSuchDevelopmentCardException if the specified deck is empty.
         */
        public DevelopmentCard getDevelopmentCard(int level, Color color) throws NoSuchDevelopmentCardException {
            LinkedList<DevelopmentCard> stack = this.decksMap.get(color).get(level - 1);
            if(stack.isEmpty()) throw new NoSuchDevelopmentCardException();
            else return stack.getFirst();
        }

        /**
         * Returns the DevelopmentCard on top of the specified deck;
         * the given color specifies the column as level does for the row;
         * if the deck is empty throws NoSuchDevelopmentCardException.
         * @param level the level of the required DevelopmentCard.
         * @param color the color of the required DevelopmentCard.
         * @return the required DevelopmentCard.
         * @throws NoSuchDevelopmentCardException if the specified deck is empty.
         */
        public DevelopmentCard buyDevelopmentCard(int level, Color color) throws NoSuchDevelopmentCardException {
            LinkedList<DevelopmentCard> stack = this.decksMap.get(color).get(level - 1);
            if(stack.isEmpty()) throw new NoSuchDevelopmentCardException();
            else return stack.pollFirst();
        }

        /**
         * Tests if no Development Cards of the specified Color are available.
         * @param color the Color whose availability is to test.
         * @return true if no more Development Cards of the specified Color are available, false otherwise.
         */
        public boolean isOutOfStock(Color color) {
            for(List<DevelopmentCard> level : this.decksMap.get(color))
                if(!level.isEmpty()) return false;
            return true;
        }

        /**
         * Drops the specified amount of DevelopmentCards from the column
         * corresponding to the given Color;
         * Cards are discarded from lower levels and then in order;
         * returns false if the specified column has no more DevelopmentCards, true otherwise.
         * @param color the Color corresponding to the column to discard cards from.
         * @param amount the amount of DevelopmentCards to discard.
         * @return false if the specified column is left empty, true otherwise.
         */
        public boolean discard(Color color, int amount) {
            int level = 0;
            List<LinkedList<DevelopmentCard>> column = this.decksMap.get(color);
            for(int i = 0; i < amount; i++) {
                if(column.get(level).pollFirst() == null) {
                    // It is necessary to remove from a higher level
                    level++;
                    if(level == 3) return false; // The column is empty

                    // Since the card has not been discarded
                    // It is necessary to redo this iteration
                    i--;
                }
            }
            return !this.isOutOfStock(color);
        }

        @Override
        public String toString() {
            StringBuilder res = new StringBuilder("{");
            for(int row = 2; row >= 0; row--) {
                res.append("\n\t");
                for(Color color : Color.values()) {
                    res.append(" ").append(this.decksMap.get(color).get(row).size());
                }
            }
            res.append("\n}");
            return res.toString();
        }
    }

    /**
     * Constructs a MarketBoard with all the necessary resources and DevelopmentCards.
     */
    public MarketBoard() {
        this.resourceMarket = new ResourceMarket();
        this.cardMarket = new CardMarket();
    }

    /**
     * Returns the pack of resources gained from the specified row.
     * The resource market shifting is handled automatically.
     * @param row the row to take.
     * @return the ResourcePack of gathered resources.
     */
    public ResourcePack takeRow(int row) {
        return this.resourceMarket.getRow(row);
    }

    /**
     * Returns the pack of resources gained from the specified column.
     * The resource market shifting is handled automatically.
     * @param column the column to take.
     * @return the ResourcePack of gathered resources.
     */
    public ResourcePack takeColumn(int column) {
        return this.resourceMarket.getColumn(column);
    }

    /**
     * Returns the cost of the first available DevelopmentCard from the market
     * with the specified Color and level; if there are no available cards
     * that matches the requirement throws NoSuchDevelopmentCardException.
     * @param level the level of the required DevelopmentCard.
     * @param color the color of the required DevelopmentCard.
     * @return the ResourcePack representing the cost of the required card.
     * @throws NoSuchDevelopmentCardException if no card matches the requirement.
     */
    public ResourcePack getCost(int level, Color color) throws NoSuchDevelopmentCardException {
        return this.cardMarket.getCost(level,color);
    }

    /**
     * Retrieves, but does not remove, the first available DevelopmentCard from the market
     * with the specified Color and level; if there are no available cards
     * that matches the requirement throws NoSuchDevelopmentCardException.
     * @param level the level of the required DevelopmentCard.
     * @param color the Color of the required DevelopmentCard.
     * @return the first available DevelopmentCard with the given color and level.
     * @throws NoSuchDevelopmentCardException if no card matches the requirement.
     */
    public DevelopmentCard getDevelopmentCard(int level, Color color) throws NoSuchDevelopmentCardException {
        return this.cardMarket.getDevelopmentCard(level,color);
    }

    /**
     * Returns the first available DevelopmentCard from the market
     * with the specified Color and level; if there are no available cards
     * that matches the requirement throws NoSuchDevelopmentCardException.
     * @param level the level of the required DevelopmentCard.
     * @param color the Color of the required DevelopmentCard.
     * @return the first available DevelopmentCard with the given color and level.
     * @throws NoSuchDevelopmentCardException if no card matches the requirement.
     */
    public DevelopmentCard buyDevelopmentCard(int level, Color color) throws NoSuchDevelopmentCardException {
        return this.cardMarket.buyDevelopmentCard(level,color);
    }

    /**
     * Drops the given amount of DevelopmentCards of the specified
     * Color from the current Market;
     * cards are discarded from lower levels and then in order;
     * returns false if there are no more DevelopmentCards of the given color in the current Market.
     * @param color the color of the DevelopmentCards to discard.
     * @param amount the amount of DevelopmentCards to discard.
     * @return false if there are not any DevelopmentCard of the specified color left, true otherwise.
     */
    public boolean discard(Color color, int amount) {
        return this.cardMarket.discard(color, amount);
    }

    public String getResourceMarketView() {
        return this.resourceMarket.toString();
    }

    public String getCardMarketView() {
        return this.cardMarket.toString();
    }
}
