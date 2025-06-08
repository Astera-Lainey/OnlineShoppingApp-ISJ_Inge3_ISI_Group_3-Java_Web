// Cart functionality
function addToCart(productId) {
    async function addToCartRequest() {
        try {
            const response = await fetch(`/api/cart/add/${productId}?quantity=1`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include'
            });

            if (!response.ok) {
                throw new Error('Failed to add to cart');
            }

            const result = await response.json();
            if (result.success) {
                Swal.fire({
                    icon: 'success',
                    title: 'Success!',
                    text: 'Product added to cart',
                    showConfirmButton: false,
                    timer: 1500
                });
            } else {
                throw new Error(result.error || 'Failed to add to cart');
            }
        } catch (error) {
            console.error('Error adding to cart:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: error.message || 'Failed to add to cart'
            });
        }
    }

    addToCartRequest();
}

async function removeFromCart(productId) {
    try {
        const response = await fetch(`/api/cart/remove/${productId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Failed to remove item from cart');
        }

        const result = await response.json();
        if (result.success) {
            // Reload the page to update the cart
            window.location.reload();
        } else {
            alert(result.error || 'Failed to remove item from cart');
        }
    } catch (error) {
        console.error('Error removing from cart:', error);
        alert('Failed to remove item from cart');
    }
}

async function updateCartQuantity(productId, quantity) {
    try {
        const response = await fetch(`/api/cart/update/${productId}?quantity=${quantity}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Failed to update cart');
        }

        const result = await response.json();
        if (result.success) {
            // Reload the page to update the cart
            window.location.reload();
        } else {
            alert(result.error || 'Failed to update cart');
        }
    } catch (error) {
        console.error('Error updating cart:', error);
        alert('Failed to update cart');
    }
} 