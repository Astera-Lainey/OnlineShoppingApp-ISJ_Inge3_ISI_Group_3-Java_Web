// Wishlist functionality
const WishlistManager = {
    // Add product to wishlist
    addToWishlist: async function(productId, quantity = 1) {
        try {
            const response = await fetch('/api/wishlist/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify({ productId, quantity })
            });

            if (!response.ok) {
                throw new Error('Failed to add to wishlist');
            }

            const data = await response.json();
            Swal.fire({
                icon: 'success',
                title: 'Added to Wishlist',
                text: 'Product has been added to your wishlist!',
                timer: 2000,
                showConfirmButton: false
            });
            return data;
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: error.message || 'Failed to add to wishlist'
            });
            throw error;
        }
    },

    // Get user's wishlist
    getWishlist: async function() {
        try {
            const response = await fetch('/api/wishlist', {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (!response.ok) {
                throw new Error('Failed to fetch wishlist');
            }

            return await response.json();
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Failed to load wishlist'
            });
            throw error;
        }
    },

    // Update wishlist item quantity
    updateQuantity: async function(wishlistItemId, quantity) {
        try {
            const response = await fetch(`/api/wishlist/${wishlistItemId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify({ quantity })
            });

            if (!response.ok) {
                throw new Error('Failed to update wishlist');
            }

            return await response.json();
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Failed to update wishlist item'
            });
            throw error;
        }
    },

    // Remove item from wishlist
    removeFromWishlist: async function(wishlistItemId) {
        try {
            const result = await Swal.fire({
                title: 'Remove from Wishlist?',
                text: 'Are you sure you want to remove this item from your wishlist?',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: 'Yes, remove it!'
            });

            if (result.isConfirmed) {
                const response = await fetch(`/api/wishlist/${wishlistItemId}`, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    }
                });

                if (!response.ok) {
                    throw new Error('Failed to remove from wishlist');
                }

                Swal.fire(
                    'Removed!',
                    'Item has been removed from your wishlist.',
                    'success'
                );
                return true;
            }
            return false;
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Failed to remove item from wishlist'
            });
            throw error;
        }
    },

    // Check if product is in wishlist
    isInWishlist: async function(productId) {
        try {
            const response = await fetch(`/api/wishlist/check/${productId}`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (!response.ok) {
                throw new Error('Failed to check wishlist status');
            }

            return await response.json();
        } catch (error) {
            console.error('Error checking wishlist status:', error);
            return false;
        }
    },

    // Render wishlist items
    renderWishlist: async function(containerId) {
        try {
            const wishlist = await this.getWishlist();
            const container = document.getElementById(containerId);
            
            if (!wishlist.length) {
                container.innerHTML = `
                    <div class="text-center py-5">
                        <i class="fas fa-heart-broken fa-3x text-muted mb-3"></i>
                        <h4>Your wishlist is empty</h4>
                        <p class="text-muted">Add some products to your wishlist to see them here!</p>
                    </div>
                `;
                return;
            }

            container.innerHTML = wishlist.map(item => `
                <div class="card mb-3 wishlist-item" data-id="${item.id}">
                    <div class="row g-0">
                        <div class="col-md-2">
                            <img src="${item.productImage}" class="img-fluid rounded-start" alt="${item.productName}">
                        </div>
                        <div class="col-md-10">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-start">
                                    <h5 class="card-title">${item.productName}</h5>
                                    <button class="btn btn-outline-danger btn-sm remove-wishlist-item">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </div>
                                <p class="card-text text-muted">$${item.productPrice.toFixed(2)}</p>
                                <div class="d-flex align-items-center">
                                    <label class="me-2">Quantity:</label>
                                    <input type="number" class="form-control form-control-sm quantity-input" 
                                           style="width: 80px" value="${item.quantity}" min="1">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            `).join('');

            // Add event listeners
            container.querySelectorAll('.remove-wishlist-item').forEach(button => {
                button.addEventListener('click', async (e) => {
                    const itemId = e.target.closest('.wishlist-item').dataset.id;
                    if (await this.removeFromWishlist(itemId)) {
                        e.target.closest('.wishlist-item').remove();
                    }
                });
            });

            container.querySelectorAll('.quantity-input').forEach(input => {
                input.addEventListener('change', async (e) => {
                    const itemId = e.target.closest('.wishlist-item').dataset.id;
                    const quantity = parseInt(e.target.value);
                    if (quantity > 0) {
                        await this.updateQuantity(itemId, quantity);
                    } else {
                        e.target.value = 1;
                    }
                });
            });

        } catch (error) {
            console.error('Error rendering wishlist:', error);
        }
    }
};

// Add to wishlist button handler
document.addEventListener('DOMContentLoaded', function() {
    // Add wishlist button to product cards
    document.querySelectorAll('.add-to-wishlist-btn').forEach(button => {
        button.addEventListener('click', async (e) => {
            e.preventDefault();
            const productId = e.target.dataset.productId;
            try {
                await WishlistManager.addToWishlist(productId);
                // Update button state
                e.target.classList.add('active');
                e.target.disabled = true;
            } catch (error) {
                console.error('Error adding to wishlist:', error);
            }
        });
    });

    // Initialize wishlist page if on wishlist page
    const wishlistContainer = document.getElementById('wishlist-container');
    if (wishlistContainer) {
        WishlistManager.renderWishlist('wishlist-container');
    }
}); 