import { useState } from 'react'
import { Send, Upload, MessageSquare, FileText, Settings } from 'lucide-react'

function App() {
    const [message, setMessage] = useState('')
    const [messages, setMessages] = useState<Array<{ role: string; content: string }>>([
        { role: 'assistant', content: 'Hello! I am your Phoenix AI assistant. Upload your documents and ask me anything about them.' }
    ])

    const handleSend = () => {
        if (!message.trim()) return

        setMessages(prev => [...prev, { role: 'user', content: message }])
        setMessage('')

        // TODO: Implement actual API call with SSE streaming
        setTimeout(() => {
            setMessages(prev => [...prev, {
                role: 'assistant',
                content: 'This is a placeholder response. Connect the backend to enable real RAG-powered responses.'
            }])
        }, 1000)
    }

    return (
        <div className="flex h-screen bg-slate-900">
            {/* Sidebar */}
            <aside className="w-64 bg-slate-800 border-r border-slate-700 flex flex-col">
                <div className="p-4 border-b border-slate-700">
                    <h1 className="text-xl font-bold text-orange-500">Phoenix</h1>
                    <p className="text-sm text-slate-400">Knowledge Platform</p>
                </div>

                <nav className="flex-1 p-4 space-y-2">
                    <button className="w-full flex items-center gap-3 px-3 py-2 rounded-lg bg-slate-700 text-white">
                        <MessageSquare size={18} />
                        <span>Chat</span>
                    </button>
                    <button className="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-slate-400 hover:bg-slate-700 hover:text-white transition">
                        <FileText size={18} />
                        <span>Documents</span>
                    </button>
                    <button className="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-slate-400 hover:bg-slate-700 hover:text-white transition">
                        <Upload size={18} />
                        <span>Upload</span>
                    </button>
                </nav>

                <div className="p-4 border-t border-slate-700">
                    <button className="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-slate-400 hover:bg-slate-700 hover:text-white transition">
                        <Settings size={18} />
                        <span>Settings</span>
                    </button>
                </div>
            </aside>

            {/* Main Chat Area */}
            <main className="flex-1 flex flex-col">
                {/* Messages */}
                <div className="flex-1 overflow-y-auto p-6 space-y-4">
                    {messages.map((msg, idx) => (
                        <div
                            key={idx}
                            className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}
                        >
                            <div
                                className={`max-w-2xl px-4 py-3 rounded-2xl ${msg.role === 'user'
                                        ? 'bg-orange-500 text-white'
                                        : 'bg-slate-700 text-slate-100'
                                    }`}
                            >
                                {msg.content}
                            </div>
                        </div>
                    ))}
                </div>

                {/* Input Area */}
                <div className="p-4 border-t border-slate-700">
                    <div className="max-w-4xl mx-auto flex gap-3">
                        <input
                            type="text"
                            value={message}
                            onChange={(e) => setMessage(e.target.value)}
                            onKeyDown={(e) => e.key === 'Enter' && handleSend()}
                            placeholder="Ask a question about your documents..."
                            className="flex-1 px-4 py-3 bg-slate-800 border border-slate-600 rounded-xl text-white placeholder-slate-400 focus:outline-none focus:border-orange-500 transition"
                        />
                        <button
                            onClick={handleSend}
                            className="px-4 py-3 bg-orange-500 hover:bg-orange-600 rounded-xl text-white transition flex items-center gap-2"
                        >
                            <Send size={18} />
                            <span>Send</span>
                        </button>
                    </div>
                </div>
            </main>
        </div>
    )
}

export default App
