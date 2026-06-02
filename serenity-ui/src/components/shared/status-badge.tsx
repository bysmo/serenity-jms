'use client';

import { Badge } from '@/components/ui/badge';
import { STATUT_COLORS } from '@/lib/constants';

interface StatusBadgeProps {
  statut: string;
  label?: string;
  className?: string;
}

export function StatusBadge({ statut, label, className }: StatusBadgeProps) {
  const colorClass = STATUT_COLORS[statut] || 'bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-300';

  return (
    <Badge variant="secondary" className={`${colorClass} border-0 font-medium ${className || ''}`}>
      {label || statut.replace(/_/g, ' ')}
    </Badge>
  );
}
