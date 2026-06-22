export interface Coupon {
    id: number;
    code: string;
    description: string;
    discountType: 'PERCENT' | 'AMOUNT';
    discountValue: number;
    minOrderValue: number;
    validFrom: string;
    validTo: string;
    isActive: boolean;
}
