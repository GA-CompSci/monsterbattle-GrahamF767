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
        gui = new MonsterBattleGUI("Monster Battle - Mr. A");

        // CHOOSE DIFFICULTY (number of monsters to face)
        int numMonsters = chooseDifficulty();
        monsters = new ArrayList<>();
    
        
        for(int k = 0; k < numMonsters; k++) {
            if(k == 0) {
                // add a monster with a special ability
                monsters.add(new Monster("Vampire"));
            } else if (k == 1) {
                monsters.add(new Monster("Armored"));
            } else if (k == 2) {
                monsters.add(new Monster("Raged"));
            }
        }
        gui.updateMonsters(monsters);  

        // PICK YOUR CHARACTER BUILD (using the 4 action buttons!)
        pickCharacterBuild(); 


        // TODO: Create starting items
        inventory = new ArrayList<>();
        // Add items here! Look at GameDemo.java for examples
        gui.updateInventory(inventory);
        
        // ACTION BUTTONS
        String[] buttons = {"Attack (" + playerDamage + ")", 
                            "Defend (" + playerShield + ")", 
                            "Heal (" + playerHeal + ")", 
                            "Special Ability"};
        gui.setActionButtons(buttons);
        
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
                numMonsters = (int)(Math.random() * (4-2+1)) + 2;
                break;
            case 1: 
                numMonsters = (int)(Math.random() * (5-4+1)) + 4;
                break;
            case 2:
                numMonsters = (int)(Math.random() * (7-6+1)) + 6;
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
            gui.displayMessage("You chose Fighter! High damage, but weak defense.");
            playerShield -= (int)(Math.random() * 20 + 1) + 5;  // Reduce shield by 5-25
            playerHeal -= (int)(Math.random() * 20 + 1) + 5;    // Reduce heal by 5-25

            playerSpeed = (int)(Math.random() * 20 + 1) + 5;
        } else if (choice == 1) {
            // Tank: high shield, low damage and speed
            gui.displayMessage("You chose Tank! Tough defense, but slow attacks.");
            playerSpeed -= (int)(Math.random() * 9) + 1;        // Reduce speed by 1-9
            playerDamage -= (int)(Math.random() * 20 + 1) + 5;  // Reduce damage by 5-25
            playerSpeed = (int)(Math.random() * 9) + 1;
        } else if (choice == 2) {
            // Healer: high healing, low damage and shield
            gui.displayMessage("You chose Healer! Great recovery, but fragile.");
            playerDamage -= (int)(Math.random() * 21) + 5;      // Reduce damage by 5-25
            playerShield -= (int)(Math.random() * 10) + 1;      // Reduce shield by 5-25
        } else {
            // Ninja: high speed, low healing and health
            gui.displayMessage("You chose Ninja! Fast and deadly, but risky.");
            playerHeal -= (int)(Math.random() * 21) + 5;        // Reduce heal by 5-25
            playerHealth -= (int)(Math.random() * 21) + 5;  
            playerSpeed = (int)(Math.random() * 6) + 6;
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
            case 3: // Use Special button
                useSpecial();
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
     * Use player special (in progress)
     */
    private void useSpecial() {
        if (inventory.isEmpty()) {
            gui.displayMessage("No items in inventory!");
            return;
        }
        
        // Use first item
        Item item = inventory.remove(0);
        gui.updateInventory(inventory);
        item.use();  // The item knows what to do!
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
                damageTaken *= 1.5;
                gui.displayMessage("The Raged monster deals 50% more damage to you!");
            }


            // SHIELD MITIGATION AND APPLY DAMAGE TO PLAYER
            if (shieldPower > 0) {
                double absorbance = Math.min(damageTaken, shieldPower);
                damageTaken -= absorbance;
                shieldPower -= absorbance;
                gui.displayMessage("You block for " + absorbance + " damage. You have " + shieldPower + " shield left.");
            }
            if (damageTaken > 0) {
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
     * Find all living monsters with health below 30
     *
     * @return ArrayList of monsters with health < 30
     */
    private ArrayList<Monster> getWeakMonsters() {
        ArrayList<Monster> result = new ArrayList<>();
        for (Monster m : monsters) {
            // consider only living monsters with health < 30
            if (m.health() > 0 && m.health() < 30) {
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
    

private ArrayList<Monster> getHealthyMonster() {
    ArrayList<Monster> result = new ArrayList<>();
    for(Monster m : monsters) {
        if(m.health() > 0 && m.health() > 50) {
            result.add(m);

        }
    }
    return result;
}

// get monster with strong damage

private ArrayList<Monster> getStrongMonster() {
    ArrayList<Monster> result = new ArrayList<>();
    for(Monster m : monsters) {
        if (m.damage > 30) {
            result.add(m);
        }
    }
    return result;
}















    // TODO: Add more helper methods as you need them!
    // Examples:
    // - Method to find the strongest monster
    // - Method to check if player has a specific item
    // - Method to add special effects
    // - etc.
}
