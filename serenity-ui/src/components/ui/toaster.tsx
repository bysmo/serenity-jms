"use client"

import { useToast } from "@/hooks/use-toast"
import {
  Toast,
  ToastClose,
  ToastDescription,
  ToastProvider,
  ToastTitle,
  ToastViewport,
} from "@/components/ui/toast"
import { CheckCircle2, AlertTriangle, AlertCircle, Info } from "lucide-react"

const icons = {
  success: <CheckCircle2 className="h-5 w-5 text-emerald-500 shrink-0" />,
  warning: <AlertTriangle className="h-5 w-5 text-amber-500 shrink-0" />,
  destructive: <AlertCircle className="h-5 w-5 text-destructive shrink-0" />,
  info: <Info className="h-5 w-5 text-blue-500 shrink-0" />,
}

export function Toaster() {
  const { toasts } = useToast()

  return (
    <ToastProvider>
      {toasts.map(function ({ id, title, description, action, ...props }) {
        const variant = props.variant || "default"
        const IconComponent = icons[variant as keyof typeof icons]

        return (
          <Toast key={id} {...props}>
            <div className="flex gap-3 items-start">
              {IconComponent}
              <div className="grid gap-1">
                {title && <ToastTitle>{title}</ToastTitle>}
                {description && (
                  <ToastDescription>{description}</ToastDescription>
                )}
              </div>
            </div>
            {action}
            <ToastClose />
          </Toast>
        )
      })}
      <ToastViewport />
    </ToastProvider>
  )
}