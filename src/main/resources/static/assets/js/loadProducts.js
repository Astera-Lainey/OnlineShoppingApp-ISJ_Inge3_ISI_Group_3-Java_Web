    const container = document.getElementById('products-container');

    products.forEach(product => {
    // Find first image for this product
    const productImages = images.filter(img => img.productId === product.id);
    const firstImageFile = productImages.length > 0 ? productImages[0].imageUrl : null;

    // Construct full image URL using your resource handler path
    const firstImageUrl = firstImageFile
    ? `/${firstImageFile}`
    : '/images/default-product.jpg'; // fallback image

    // Create product card HTML
    const productDiv = document.createElement('div');
        const categoryClass = product.category.toLowerCase().replace('_', '-');
        productDiv.className = `col-lg-3 col-md-6 text-center ${categoryClass}`;
    productDiv.innerHTML = `
<div class="single-product-item">
<a href="/user/single-product">
        <img src="${firstImageUrl}" alt="${product.name}" style="height: 200px; width: 308px;" />
      </a>
      <h3>${product.name}</h3>
      <p>Price: ${product.price} XAF</p>
      <a href="/user/cart" class="cart-btn"><i class="fas fa-shopping-cart"></i> Add to Cart</a>
</div>

    `;
    container.appendChild(productDiv);
});

