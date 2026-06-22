export interface ProductVariant{
  id: number;
  productId: number;
  sizeId: number;
  price: number;
  sizeName: string;
  sizeCode: string;
}

export interface Product {
  id: number;
  categoryId: number;
  name: string;
  description: string;
  imageUrl: string;
  isAvailable: boolean;
  isDeleted: boolean;
  createdAt: string;
  variants: ProductVariant[];
}

export interface ProductVariantRequest {
  sizeId: number;
  price: number;
}

export interface ProductRequest {
  categoryId: number;
  name: string;
  description: string;
  imageUrl: string;
  isAvailable: boolean;
  variants: ProductVariantRequest[];
}
