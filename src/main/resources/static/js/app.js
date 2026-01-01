const API_BASE = '/api';
let currentWalletAddress = localStorage.getItem('walletAddress') || null;

// Pet type to emoji mapping
const PET_EMOJIS = {
    'Dragon': '🐉',
    'Cat': '🐱',
    'Dog': '🐶',
    'Bird': '🐦',
    'Fish': '🐠',
    'Tiger': '🐯',
    'Lion': '🦁',
    'Wolf': '🐺',
    'Eagle': '🦅',
    'Shark': '🦈',
    'Fox': '🦊',
    'Bear': '🐻',
    'Rabbit': '🐰',
    'Turtle': '🐢',
    'Snake': '🐍'
};

// Color to CSS color mapping
const COLOR_MAP = {
    'Red': '#ff6b6b',
    'Blue': '#4dabf7',
    'Green': '#51cf66',
    'Yellow': '#ffd43b',
    'Purple': '#9775fa',
    'Orange': '#ff922b',
    'Pink': '#f783ac',
    'Black': '#495057',
    'White': '#f8f9fa',
    'Gold': '#ffd700',
    'Silver': '#c0c0c0',
    'Brown': '#8b4513',
    'Gray': '#868e96',
    'Cyan': '#15aabf',
    'Magenta': '#e64980'
};

// Get pet emoji
function getPetEmoji(petType) {
    return PET_EMOJIS[petType] || '🐾';
}

// Get color style
function getColorStyle(color) {
    return COLOR_MAP[color] || '#667eea';
}

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    if (currentWalletAddress) {
        displayWallet(currentWalletAddress);
    }
    loadStats();
});

function showTab(tabName, clickedButton) {
    // Hide all tabs
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // Show selected tab
    document.getElementById(tabName + '-tab').classList.add('active');
    
    // Activate the correct button
    if (clickedButton) {
        clickedButton.classList.add('active');
    } else {
        // Find the button by tab name if called programmatically
        const buttons = document.querySelectorAll('.tab-btn');
        const tabNames = ['wallet', 'pets', 'create', 'trade', 'stats'];
        const index = tabNames.indexOf(tabName);
        if (index !== -1 && buttons[index]) {
            buttons[index].classList.add('active');
        }
    }
}

async function createWallet() {
    try {
        const response = await fetch(API_BASE + '/wallet/create', {
            method: 'POST'
        });
        const data = await response.json();
        currentWalletAddress = data.address;
        localStorage.setItem('walletAddress', currentWalletAddress);
        displayWallet(currentWalletAddress);
        showMessage('wallet-info', 'Wallet created successfully!', 'success');
    } catch (error) {
        showMessage('wallet-info', 'Error creating wallet: ' + error.message, 'error');
    }
}

function displayWallet(address) {
    document.getElementById('wallet-info').style.display = 'none';
    document.getElementById('wallet-address').style.display = 'block';
    document.getElementById('address-display').textContent = address;
}

function copyAddress() {
    const address = document.getElementById('address-display').textContent;
    navigator.clipboard.writeText(address).then(() => {
        alert('Address copied to clipboard!');
    });
}

async function createPet(event) {
    event.preventDefault();
    if (!currentWalletAddress) {
        showMessage('create-result', 'Please create a wallet first!', 'error');
        return;
    }
    
    const petName = document.getElementById('pet-name').value.trim();
    if (!petName) {
        showMessage('create-result', 'Please enter a pet name!', 'error');
        return;
    }
    
    const resultDiv = document.getElementById('create-result');
    resultDiv.innerHTML = '<div class="loading">⛏️ Mining block... This may take a moment.</div>';
    
    try {
        const response = await fetch(API_BASE + '/pet/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                ownerAddress: currentWalletAddress,
                petName: petName
            })
        });
        
        if (response.ok) {
            const pet = await response.json();
            const rarityEmoji = pet.rarity >= 4 ? '⭐' : pet.rarity >= 3 ? '✨' : '🌟';
            const petEmoji = getPetEmoji(pet.type);
            const colorStyle = getColorStyle(pet.color);
            
            // Show animated success message with pet visual
            const successHtml = `
                <div class="pet-creation-success">
                    <div class="created-pet-visual" style="color: ${colorStyle};">
                        ${petEmoji}
                    </div>
                    <div class="success-message">
                        <h3>${rarityEmoji} Pet "${pet.name}" Created!</h3>
                        <p><strong>Type:</strong> ${pet.type} | <strong>Color:</strong> <span style="color: ${colorStyle};">${pet.color}</span> | <strong>Rarity:</strong> ${pet.rarity}/5</p>
                    </div>
                </div>
            `;
            document.getElementById('create-result').innerHTML = successHtml;
            document.getElementById('pet-name').value = '';
            // Auto-refresh pets list if on that tab
            if (document.getElementById('pets-tab').classList.contains('active')) {
                setTimeout(loadMyPets, 1000);
            }
        } else {
            const error = await response.json();
            showMessage('create-result', 'Error: ' + (error.error || error.message || 'Failed to create pet'), 'error');
        }
    } catch (error) {
        showMessage('create-result', 'Network error: ' + error.message, 'error');
    }
}

async function loadMyPets() {
    if (!currentWalletAddress) {
        document.getElementById('pets-grid').innerHTML = '<p>Please create a wallet first!</p>';
        return;
    }
    
    const grid = document.getElementById('pets-grid');
    grid.innerHTML = '<div class="loading">🔄 Loading pets...</div>';
    
    try {
        const response = await fetch(API_BASE + '/pets/owner/' + currentWalletAddress);
        const pets = await response.json();
        
        if (pets.length === 0) {
            grid.innerHTML = '<p>You don\'t have any pets yet. Create one in the "Create Pet" tab!</p>';
            return;
        }
        
        grid.innerHTML = pets.map((pet, index) => {
            const rarityEmoji = pet.rarity >= 4 ? '⭐' : pet.rarity >= 3 ? '✨' : '🌟';
            const petEmoji = getPetEmoji(pet.type);
            const colorStyle = getColorStyle(pet.color);
            const rarityClass = `rarity-${pet.rarity}`;
            const animationDelay = index * 0.1;
            
            return `
            <div class="pet-card ${rarityClass}" style="animation-delay: ${animationDelay}s; border-left: 4px solid ${colorStyle};">
                <div class="pet-visual">
                    <div class="pet-emoji" style="color: ${colorStyle}; filter: drop-shadow(0 0 8px ${colorStyle}40) drop-shadow(0 0 12px ${colorStyle}20);">
                        ${petEmoji}
                    </div>
                    <div class="pet-glow" style="background: radial-gradient(circle, ${colorStyle}30 0%, transparent 70%);"></div>
                </div>
                <h3 style="color: ${colorStyle};">${pet.name}</h3>
                <div class="pet-info"><strong>Type:</strong> ${pet.type}</div>
                <div class="pet-info"><strong>Color:</strong> <span style="color: ${colorStyle}; font-weight: bold; text-shadow: 0 0 4px ${colorStyle}40;">${pet.color}</span></div>
                <div class="pet-info"><strong>Rarity:</strong> ${rarityEmoji} <span class="rarity-${pet.rarity}">${pet.rarity}/5</span></div>
                <div class="pet-id"><strong>ID:</strong> <code style="font-size: 0.75rem; background: rgba(0,0,0,0.05); padding: 2px 6px; border-radius: 4px;">${pet.id.substring(0, 8)}...</code></div>
                <button onclick="viewPetHistory('${pet.id}')" class="btn btn-secondary" style="margin-top: 10px; width: 100%;">📜 View History</button>
            </div>
        `}).join('');
    } catch (error) {
        grid.innerHTML = '<div class="message error">❌ Error loading pets: ' + error.message + '</div>';
    }
}

async function tradePet(event) {
    event.preventDefault();
    if (!currentWalletAddress) {
        showMessage('trade-result', 'Please create a wallet first!', 'error');
        return;
    }
    
    const petId = document.getElementById('trade-pet-id').value.trim();
    const toOwner = document.getElementById('trade-to-owner').value.trim();
    
    if (!petId) {
        showMessage('trade-result', 'Please enter a pet ID!', 'error');
        return;
    }
    
    if (!toOwner) {
        showMessage('trade-result', 'Please enter a recipient address!', 'error');
        return;
    }
    
    const resultDiv = document.getElementById('trade-result');
    resultDiv.innerHTML = '<div class="loading">⛏️ Processing trade... Mining block...</div>';
    
    try {
        const response = await fetch(API_BASE + '/pet/trade', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                petId: petId,
                fromOwner: currentWalletAddress,
                toOwner: toOwner
            })
        });
        
        const data = await response.json();
        if (response.ok && data.status === 'success') {
            showMessage('trade-result', '✅ Pet traded successfully!', 'success');
            document.getElementById('trade-pet-id').value = '';
            document.getElementById('trade-to-owner').value = '';
            // Auto-refresh pets list if on that tab
            if (document.getElementById('pets-tab').classList.contains('active')) {
                setTimeout(loadMyPets, 1000);
            }
        } else {
            showMessage('trade-result', '❌ Error: ' + (data.message || 'Trade failed'), 'error');
        }
    } catch (error) {
        showMessage('trade-result', 'Network error: ' + error.message, 'error');
    }
}

async function viewHistory() {
    const input = document.getElementById('history-input').value.trim();
    if (!input) {
        showMessage('history-results', 'Please enter a pet ID or owner address', 'error');
        return;
    }
    
    const resultsDiv = document.getElementById('history-results');
    resultsDiv.innerHTML = '<div class="loading">🔄 Loading transaction history...</div>';
    
    try {
        // Try as pet ID first
        let response = await fetch(API_BASE + '/pet/' + input + '/history');
        let transactions = [];
        
        if (response.ok) {
            transactions = await response.json();
        } else {
            // Try as owner address
            response = await fetch(API_BASE + '/owner/' + input + '/transactions');
            if (response.ok) {
                transactions = await response.json();
            } else {
                throw new Error('Not found');
            }
        }
        
        if (transactions.length === 0) {
            resultsDiv.innerHTML = '<p>No transactions found.</p>';
            return;
        }
        
        resultsDiv.innerHTML = transactions.map((tx, index) => {
            const txEmoji = tx.type === 'CREATE_PET' ? '✨' : '🔄';
            const animationDelay = index * 0.1;
            return `
            <div class="transaction-item" style="animation-delay: ${animationDelay}s; animation: slideInUp 0.4s ease-out forwards; opacity: 0;">
                <div class="tx-type">${txEmoji} ${tx.type}</div>
                <div class="tx-details">
                    <strong>Pet ID:</strong> ${tx.petId}<br>
                    ${tx.type === 'CREATE_PET' ? `<strong>Owner:</strong> ${tx.ownerAddress}<br><strong>Pet Name:</strong> ${tx.petName}` : ''}
                    ${tx.type === 'TRADE_PET' ? `<strong>From:</strong> ${tx.fromOwner}<br><strong>To:</strong> ${tx.toOwner}` : ''}
                    <br><strong>Block Hash:</strong> ${tx.blockHash.substring(0, 16)}...
                    <br><strong>Time:</strong> ${new Date(tx.blockTimestamp).toLocaleString()}
                </div>
            </div>
        `}).join('');
    } catch (error) {
        resultsDiv.innerHTML = '<div class="message error">Error loading history: ' + error.message + '</div>';
    }
}

async function viewPetHistory(petId) {
    document.getElementById('history-input').value = petId;
    showTab('trade'); // Use the showTab function
    await viewHistory();
}

async function loadStats() {
    const statsDiv = document.getElementById('stats-display');
    statsDiv.innerHTML = '<div class="loading">🔄 Loading statistics...</div>';
    
    try {
        const response = await fetch(API_BASE + '/stats');
        const stats = await response.json();
        
        statsDiv.innerHTML = `
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-label">Total Pets</div>
                    <div class="stat-value">${stats.totalPets}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Total Owners</div>
                    <div class="stat-value">${stats.totalOwners}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Transactions</div>
                    <div class="stat-value">${stats.totalTransactions}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Blockchain Size</div>
                    <div class="stat-value">${stats.blockchainSize}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Difficulty</div>
                    <div class="stat-value">${stats.difficulty}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Valid</div>
                    <div class="stat-value">${stats.blockchainValid ? '✓' : '✗'}</div>
                </div>
            </div>
        `;
    } catch (error) {
        statsDiv.innerHTML = '<div class="message error">Error loading stats: ' + error.message + '</div>';
    }
}

function showMessage(containerId, message, type) {
    const container = document.getElementById(containerId);
    container.innerHTML = `<div class="message ${type}">${message}</div>`;
}

