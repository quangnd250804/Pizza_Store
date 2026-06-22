export interface OrderDetailRequest {
  productId?: number;
  comboId?: number;
  sizeId?: number;
  quantity: number;
  toppingIds?: number[];
}

export interface OrderRequest {
  userId?: number;
  customerName: string;
  customerPhone: string;
  shippingAddress: string;
  note?: string;
  paymentMethod: string;
  couponCode?: string;
  orderDetails: OrderDetailRequest[];
}

export enum OrderStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  PREPARING = 'PREPARING',
  READY = 'READY',
  SHIPPING = 'SHIPPING',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED'
}

export interface OrderToppingResponse {
  toppingId: number;
  name: string;
  price: number;
}

export interface OrderDetailResponse {
  id: number;
  orderId: number;
  productId?: number;
  productName?: string;
  productImageUrl?: string;
  sizeId?: number;
  sizeName?: string;
  comboId?: number;
  comboName?: string;
  comboImageUrl?: string;
  quantity: number;
  price: number;
  toppingDetails?: OrderToppingResponse[];
}

export interface OrderResponse {
  id: number;
  userId: number;
  customerName: string;
  customerPhone: string;
  shippingAddress: string;
  note?: string;
  totalPrice: number;
  status: OrderStatus;
  paymentStatus: string;
  paymentMethod: string;
  createdAt: string;
  updatedAt: string;
  couponId?: number;
  discountApplied?: number;
  orderDetails?: OrderDetailResponse[];
}

export interface PageResponse<T> {
  content: T[];
  currentPage: number;
  totalPages: number;
  totalElements: number;
}
