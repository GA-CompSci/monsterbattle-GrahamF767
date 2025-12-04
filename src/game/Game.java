package game;
import java.util.ArrayList;

import gui.MonsterBattleGUI;

/**
 * Game - YOUR monster battle game!
 * 
 * Build your game here. Look at GameDemo.java for examples.
 * 
 * Steps:
 * 1. Fill in setupGame() - create monsters, items, set health
 * 2. Fill in the action methods - what happens when player acts?
 * 3. Customize the game loop if you want
 * 4. Add your own helper methods
 * 
 * Run this file to play YOUR game
 */
public class Game {
    
    // The GUI (I had AI build most of this)
    private MonsterBattleGUI gui;
    
    // Game state - YOU manage these
    private ArrayList<Monster> monsters;
    private Monster lastAttacked; // store the monster we last attacked so it can respond
    private double shieldPower = 0;
    private ArrayList<Item> inventory;
    private int playerHealth;
    private int playerSpeed;
    private int playerDamage;
    private int playerHeal;    
    private int playerShield;
    private boolean isGolem = false;

    /**
     * Main method - start YOUR game!
     */
    public static void main(String[] args) {
        Game game = new Game(); // it instantiates a copy of this file. We're not running static
        game.play(); // this extra step is unnecessary AI stuff
    }
    
    /**
     * Play the game!
     */
    public void play() {
        setupGame();
        gameLoop();
    }
    
    /**
     * Setup - create the GUI and initial game state
     * 
     * TODO: Customize this! How many monsters? What items? How much health?
     */
    private void setupGame() {
        // Create the GUI
        gui = new MonsterBattleGUI("Monster Battle - Graham Fistek");

        // CHOOSE DIFFICULTY (number of monsters to face)
        int numMonsters = chooseDifficulty();
        monsters = new ArrayList<>();
    
        for(int k = 0; k < numMonsters; k++) {
            // make a random number for monsters
            double randMcRandomRandALot = Math.random();
            if(randMcRandomRandALot < 0.2) {
                // add a monster with a special ability
                monsters.add(new Monster("Vampire"));
            } else if (randMcRandomRandALot < 0.4) {
                monsters.add(new Monster("Armored"));
            } else if (randMcRandomRandALot < 0.6) {
                monsters.add(new Monster("Raged"));
            } else {
                monsters.add(new Monster()); // <-- you forgot this
            }
        }
        gui.updateMonsters(monsters);  

        // PICK YOUR CHARACTER BUILD (using the 4 action buttons!)
        pickCharacterBuild(); 

        inventory = new ArrayList<>();
        // Add items here! Look at GameDemo.java for examples
        gui.updateInventory(inventory);
        
        // ACTION BUTTONS
        String[] buttons = {"Attack (" + playerDamage + ")", 
                            "Defend (" + playerShield + ")", 
                            "Heal (" + playerHeal + ")", 
                            "Use Item"};
        gui.setActionButtons(buttons);
        
        // Create items
inventory = new ArrayList<>();

// Add 3 Damage Potions
for (int i = 0; i < 1; i++) {
    addDamagePotion();
}

// Add 3 Fireballs
for (int i = 0; i < 1; i++) {
    addFireball(40);
}

// Add 3 Healing Potions
for (int i = 0; i < 1; i++) {
    addHealingPotion(50); // heals 50 HP each
}

gui.updateInventory(inventory);


        // Welcome message
        gui.displayMessage("Battle Start! Make your move.");
    }
    
    /**
     * Main game loop
     * 
     * This controls the flow: player turn → monster turn → check game over
     * You can modify this if you want!
     */
    private void gameLoop() {
        // Keep playing while monsters alive and player alive
        while (countLivingMonsters() > 0 && playerHealth > 0) {
            shieldPower = 0; // start of turn, lower shield 
    
            // PLAYER'S TURN
            gui.displayMessage("Your turn! HP: " + playerHealth);
            int action = gui.waitForAction();  // Wait for button click (0-3)
            handlePlayerAction(action);
            gui.updateMonsters(monsters);
            gui.pause(500);
            
            // MONSTER'S TURN (if any alive and player alive)
            if (countLivingMonsters() > 0 && playerHealth > 0) {
                monsterAttack();
                gui.updateMonsters(monsters);
                gui.pause(500);
            }
        }
        
        // Game over!
        if (playerHealth <= 0) {
            gui.displayMessage("💀 DEFEAT! You have been defeated...");
        } else {
            gui.displayMessage("🎉 VICTORY! You defeated all monsters!");
        }
    }
    
    /**
     * Let player choose difficulty (number of monsters) using the 4 buttons
     * This demonstrates using the GUI for menu choices!
     */
    private int chooseDifficulty() {
        // Set button labels to difficulty levels
        String[] difficulties = {"Bottom Ladder (2-4)", "Mid-Ladder (4-5)", "COOKED (6-7)", "Be there or be 💀 (10-15)"};
        gui.setActionButtons(difficulties);
        
        // Display choice prompt
        gui.displayMessage("---- CHOOSE DIFFICULTY ----");
        
        // Wait for player to click a button (0-3)
        int choice = gui.waitForAction();
        int numMonsters = 0;
        switch(choice){
            case 0:
                numMonsters = (int)(Math.random() * (2+1)) + 2; // 2-4
                break;
            case 1: 
                numMonsters = (int)(Math.random() * (5-4+1)) + 4; // 4-5
                break;
            case 2:
                numMonsters = (int)(Math.random() * (7-6+1)) + 6; // 6-7
                break;
            case 3:
                numMonsters = (int)(Math.random() * (15-10+1)) + 10;
                break;
        }
        
        gui.displayMessage("You will face " + numMonsters + " monsters! Good luck!");
        gui.pause(1500);
        
        return numMonsters;
    }
    
        /**
     * Let player pick their character build using the 4 buttons
     * This demonstrates using the GUI for menu choices!
     */
    private void pickCharacterBuild() {
        // Set button labels to character classes
        String[] characterClasses = {"P.E.K.K.A", "Sneaky Golem", "Battle Healer", "Bandit"};
        gui.setActionButtons(characterClasses);
        
        // Display choice prompt
        gui.displayMessage("---- PICK YOUR BUILD ----");
        
        // Wait for player to click a button (0-3)
        int choice = gui.waitForAction();
        
        // Initialize default stats
        playerDamage = 50;
        playerShield = 50;
        playerHeal = 20;
        playerSpeed = 10;
        playerHealth = 100;
        
        // Customize stats based on character choice
        if (choice == 0) {
            // Fighter: high damage, low healing and shield
            gui.displayMessage("You chose P.E.K.K.A! High damage, but weak defense.");
            playerShield -= (int)(Math.random() * 20 + 1) + 5;  // Reduce shield by 5-25
            playerHeal -= (int)(Math.random() * 20 + 1) + 5;    // Reduce heal by 5-25
            playerSpeed = (int)(Math.random() * 20 + 1) + 5;
            
        } else if (choice == 1) {

            // Tank: high shield, low damage and speed
            gui.displayMessage("You chose Sneaky Golem! Tough defense, but slow attacks.");
            playerSpeed -= (int)(Math.random() * 9) + 1;        // Reduce speed by 1-9
            playerDamage -= (int)(Math.random() * 20 + 1) + 5;  // Reduce damage by 5-25
            playerSpeed = (int)(Math.random() * 9) + 1;
            isGolem = true;
            
        } else if (choice == 2) {
            // Healer: high healing, low damage and shield
            gui.displayMessage("You chose Battle Healer! Great recovery, but fragile.");
            playerDamage -= (int)(Math.random() * 21) + 5;      // Reduce damage by 5-25
            playerShield -= (int)(Math.random() * 10) + 1;      // Reduce shield by 5-25
            
        } else {
            // Bandit: high speed, low healing and health
            gui.displayMessage("You chose Bandit! Fast and deadly, but risky.");
            playerHeal -= (int)(Math.random() * 21) + 5;        // Reduce heal by 5-25
            playerHealth -= (int)(Math.random() * 21) + 5;  
            playerSpeed = (int)(Math.random() * 9) + 6;
        }
        if(playerHeal < 0) playerHeal = 0;
        
        gui.setPlayerMaxHealth(playerHealth);
        gui.updatePlayerHealth(playerHealth);
        
        // Pause to let player see their choice
        gui.pause(1500);
    }
    
    /**
     * Handle player's action choice
     * 
     * TODO: What happens for each action?
     */
    private void handlePlayerAction(int action) {
        switch (action) {
            case 0: // Attack button
                attackMonster();
                break;
            case 1: // Defend button
                defend();
                break;
            case 2: // Heal button
                heal();
                break;
            case 3: // Use Item button
                useItem();
                break;
        }
    }
    
    /**
     * Attack a monster
     * 
     * - How much damage?
     * - Which monster gets hit?
     * - Special effects?
     */
    private void attackMonster() {
        // TODO: Target more intelligently
        Monster target = getRandomLivingMonster();
        lastAttacked = target;
        int damage = (int)(Math.random() * playerDamage + 1); // 0 - playerDamage
        
        // FAILED ROLL
        if(damage == 0) {
            // hurt yourself
            playerHealth -= 5;
            gui.displayMessage("Critical fail! You hit yourself for 5 points");
            gui.updatePlayerHealth(playerHealth);
        
        // CRITICAL HIT
        } else if(damage == playerDamage){
            gui.displayMessage("Critical hit! You slayed the monster");
            target.takeDamage(target.health());
        
        // SPECIALS + EVERYTHING ELSE
        } else {
            // MONSTER SPECIAL ARMORED
            if(target.special().equals("Armored")){
                //25% damage reduction
                target.takeDamage((int)(damage* .75));
                gui.displayMessage("The Armored monster's armor prevented " + (damage * .75) + " damage!");
            }
            // NO SPECIALS
            else {
                target.takeDamage(damage); 
                gui.displayMessage("You hit the monster for " + damage + " damage");
            }
        }
        
        // Show which one we hit
        int index = monsters.indexOf(target);
        gui.highlightMonster(index);
        gui.pause(300);
        gui.highlightMonster(-1);
        // update the list
        gui.updateMonsters(monsters);
    }
    
    /**
     * Defend
     * 
     * TODO: What does defending do?
     * - Reduce damage?
     * - Block next attack?
     * - Something else?
     */
    private void defend() {
        shieldPower = playerShield; // charge up my shield
        
        gui.displayMessage("Shield up! Brace for impact!");
    }
    
    /**
     * Heal yourself
     * 
     * TODO: How does healing work?
     * - How much HP?
     * - Any limits?
     */
    private void heal() {
        // TODO: Implement your heal!
        playerHealth += playerHeal;
        gui.updatePlayerHealth(playerHealth);
        
        gui.displayMessage("The heal spirit came and healed you " + playerHeal + " Health.");
    }
    
   /**
 * Use player items (choose which one)
 */
/**
 * Use player items (choose which one)
 */
/**
 * Use player items (choose which one)
 */
private void useItem() {
    if (inventory.isEmpty()) {
        gui.displayMessage("No items in inventory!");
        return;
    }

    // Build up to 3 item choices (slots 1–3)
    int maxChoices = Math.min(3, inventory.size());
    String[] itemChoices = new String[4];

    // First slot is always "Back"
    itemChoices[0] = "⬅ Back";

    // Fill remaining slots with items
    for (int i = 0; i < maxChoices; i++) {
        itemChoices[i + 1] = inventory.get(i).getIcon() + " " + inventory.get(i).getName();
    }
    // Fill unused slots with "Empty"
    for (int i = maxChoices + 1; i < 4; i++) {
        itemChoices[i] = "Empty";
    }

    gui.setActionButtons(itemChoices);
    gui.displayMessage("---- Choose an item to use ----");

    int choice = gui.waitForAction();

    if (choice == 0) {
        // Back button pressed → return to main actions
        gui.displayMessage("Cancelled item use.");
    } else if (choice <= maxChoices) {
        // Use selected item
        Item chosen = inventory.remove(choice - 1); // shift index because slot 0 is Back
        gui.updateInventory(inventory);
        chosen.use();
    } else {
        gui.displayMessage("Invalid choice!");
    }

    // Reset action buttons back to normal
    String[] buttons = {"Attack (" + playerDamage + ")", 
                        "Defend (" + playerShield + ")", 
                        "Heal (" + playerHeal + ")", 
                        "Use Item"};
    gui.setActionButtons(buttons);
}



    
    /**
     * Monster attacks player
     * - How much damage?
     * - Which monster attacks?
     * - Special abilities?
     */
    private void monsterAttack() {
        // build a list of every monster that gets to take a swipe at us
        ArrayList<Monster> attackers = getSpeedyMonsters();
        // first check if there is a lastAttacked
        if(lastAttacked != null && lastAttacked.health() > 0 && !attackers.contains(lastAttacked)) 
            attackers.add(lastAttacked);

        // MONSTERS ATTACK
        for (Monster monster : attackers) {
            int damageTaken = (int)(Math.random() * monster.damage() + 1);

            // HANDLE SPECIAL MONSTERS COMES BEFORE SHIELD MITIGATION
            //VAMPIRE SPECIAL (Heals what it attacks)
            if(monster.special().equals("Vampire")){
                monster.takeDamage(-damageTaken);
                gui.displayMessage("The Vampire healed for " + damageTaken + " health!");
            } 
            // MONSTER SPECIAL RAGED
            else if(monster.special().equals("Raged")){
                damageTaken *= 3.0;
                gui.displayMessage("The Raged monster deals 300% more damage to you!");
            }


            // SHIELD MITIGATION AND APPLY DAMAGE TO PLAYER
            if (shieldPower > 0) {
                double absorbance = Math.min(damageTaken, shieldPower);
                damageTaken -= absorbance;
                shieldPower -= absorbance;
                gui.displayMessage("You block for " + absorbance + " damage. You have " + shieldPower + " shield left.");
            }
            if (damageTaken > 0) {

                if (damageTaken > 0) {

                    // GOLEM ASPECTS
                    if (isGolem) {
                        int reduced = (int)(damageTaken * 0.80);  // takes only 80%
                        gui.displayMessage("The Golem absorbs 20% damage!");
                        damageTaken = reduced;
                        playerHealth = 150;
                    }

                playerHealth -= damageTaken;
                gui.displayMessage("Monster hits you for " + damageTaken + " damage!");
                gui.updatePlayerHealth(playerHealth);
}

                playerHealth -= damageTaken;
                gui.displayMessage("Monster hits you for " + damageTaken + " damage!");
                gui.updatePlayerHealth(playerHealth);
            } 

            int index = monsters.indexOf(monster);
            gui.highlightMonster(index);
            gui.pause(300);
            gui.highlightMonster(-1);
            gui.updateMonsters(monsters);
        }

    }
    
    // ==================== HELPER METHODS ====================
    // Add your own helper methods here!
    
    /**
     * Count how many monsters are still alive
     */
    private int countLivingMonsters() {
        int count = 0;
        for (Monster m : monsters) {
            if (m.health() > 0) count++;
        }
        return count;
    }

    private ArrayList<Monster> getSpecialMonsters() {
        ArrayList<Monster> result = new ArrayList<>();
        for(Monster m : monsters){
            if(m.special() != null && !m.special().equals("") && m.health() > 0){
                result.add(m);
            }
        }
        return result;
    }

    private ArrayList<Monster> getSpeedyMonsters() {
        ArrayList<Monster> result = new ArrayList<>();
        for(Monster m : monsters){
            if(m.speed() > playerSpeed && m.health() > 0){
                result.add(m);
            }
        }
        return result;
    }
    
    /**
     * Get a random living monster
     */
    private Monster getRandomLivingMonster() {
        ArrayList<Monster> alive = new ArrayList<>();
        for (Monster m : monsters) {
            if (m.health() > 0) alive.add(m);
        }
        if (alive.isEmpty()) return null;
        return alive.get((int)(Math.random() * alive.size()));
    }

    // --------ITEMS--------
    
    private void addFireball(int damage) {
        inventory.add(new Item("FIREBALL", "🔥", () -> {
            for (Monster m : monsters) {
                if (m.health() > 0) {
                    m.takeDamage(damage);
                }
            }
            gui.displayMessage("BAM! All monsters take " + damage + " damage!");
            gui.updateMonsters(monsters);
        }));
    }

     /**
     * Add a damage potion to inventory
     */
    private void addDamagePotion() {
        double damageBoost = playerDamage * 1.2; // 20% boost per potion
        inventory.add(new Item("Damage Potion", "💪", () -> {
            playerDamage += damageBoost;
            gui.displayMessage("💪 Used Damage Potion! Damage increased by " + damageBoost + "!");
        }));
    }
    
        /**
     * Add a healing potion to inventory
     */
    private void addHealingPotion(int healAmount) {
        inventory.add(new Item("Healing Potion", "🧪", () -> {
            playerHealth += healAmount;
            gui.updatePlayerHealth(playerHealth);
            gui.displayMessage("🧪 Used Healing Potion! Restored " + healAmount + " health.");
        }));
    }

}

